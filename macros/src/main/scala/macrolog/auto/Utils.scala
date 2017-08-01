package macrolog.auto

import scala.collection.immutable.Seq
import scala.meta._

/**
 * @author Maksim Ochenashko
 */
private[auto] object Utils {

  case class Param(mods: Seq[Mod], originalType: Type, name: Term.Name, customName: Option[Term])

  def extractParams(defn: Stat, ctorParamss: Seq[Seq[Term.Param]], stats: Option[Seq[Stat]]): Seq[Param] = {
    def isIncluded(mods: Seq[Mod]): Boolean = isAnnotated(mods, "loggable", "include")

    def isExcluded(mods: Seq[Mod]): Boolean = isAnnotated(mods, "loggable", "exclude")

    def named(mods: Seq[Mod]): Option[Term] = annotationValue(mods, "loggable", "named")

    val ctorParams =
      for {
        param <- ctorParamss.flatten.toList
        if applicableParam(param) && !isExcluded(param.mods)
      } yield Param(param.mods, extractParamType(param), Term.Name(param.name.value), named(param.mods))

    val templateParams =
      stats.getOrElse(Nil)
        .collect(Declaration.fromStat)
        .filter { d => isIncluded(d.mods) && !isExcluded(d.mods) }
        .map {
          case d@Declaration.Def(mods, name, decltpe, pos, _) =>
            if (applicableDef(d)) Param(mods, decltpe.get, name.get, named(mods))
            else abort(pos, "`private[this]` and `protected[this]` is not allowed. Def type must be declared explicitly")

          case d =>
            if (applicableDeclaration(d)) {
              d.name.fold(abort(d.pos, "Can not extract field name")) { name =>
                Param(d.mods, d.decltpe.get, name, named(d.mods))
              }
            } else {
              abort(d.pos, s"`private[this]` and `protected[this]` is not allowed. Variable type must be declared explicitly")
            }
        }

    ctorParams ++ templateParams
  }

  def appendTemplate(template: Template, stats: Seq[Stat]): Template = {
    val _stats = template.stats match {
      case Some(body) => Some(body ++ stats)
      case None       => Some(stats)
    }

    template.copy(stats = _stats)
  }

  def extractParamType(param: Term.Param): Type =
    param.decltpe match {
      case Some(t: Type)  => t
      case _              => abort(param.pos, "Field without type declaration")
    }

  def isAnnotated(mods: Seq[Mod], prefix: String, name: String): Boolean =
    mods exists {
      case Mod.Annot(Ctor.Ref.Select(Term.Name(`prefix`), Ctor.Ref.Name(`name`))) => true
      case _                                                                      => false
    }

  def annotationValue(mods: Seq[Mod], prefix: String, name: String): Option[Term] =
    mods collectFirst {
      case Mod.Annot(Term.Apply(Ctor.Ref.Select(Term.Name(`prefix`), Ctor.Ref.Name(`name`)), Seq(value: Term))) => value
    }

  private def applicableParam(param: Term.Param): Boolean =
    param.mods forall modifierMatcher

  private def applicableDeclaration(v: Declaration): Boolean =
    v.decltpe.nonEmpty && v.mods.forall(modifierMatcher)

  private def applicableDef(v: Declaration.Def): Boolean =
    v.decltpe.nonEmpty && (v.paramss.isEmpty || v.paramss.head.isEmpty) && v.mods.forall(modifierMatcher)

  private val modifierMatcher: Mod => Boolean = {
    case Mod.Private(_: Term.This)   => false
    case Mod.Protected(_: Term.This) => false
    case _                           => true
  }

}
