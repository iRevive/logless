package macrolog.auto

import scala.collection.immutable.Seq
import scala.meta._

/**
  * @author Maksim Ochenashko
  */
private[auto] object Utils {

  case class Param(mods: Seq[Mod], originalType: Type, name: Term.Name, customName: Option[Term])

  def extractParams(defn: Stat, ctorParamss: Seq[Seq[Term.Param]], stats: Option[Seq[Stat]]): Seq[Param] = {
    val constructorParamAccessibleByDefault = defn match {
      case c: Defn.Class if !isCaseClass(c.mods) => false
      case _                                     => true
    }

    def isIncluded(mods: Seq[Mod]): Boolean = isAnnotated(mods, "loggable", "include")

    def isExcluded(mods: Seq[Mod]): Boolean = isAnnotated(mods, "loggable", "exclude")

    def named(mods: Seq[Mod]): Option[Term] = annotationValue(mods, "loggable", "named")

    val ctorParams =
      for {
        param <- ctorParamss.flatten.toList
        if applicableParam(param, constructorParamAccessibleByDefault) && !isExcluded(param.mods)
      } yield Param(param.mods, extractParamType(param), Term.Name(param.name.value), named(param.mods))

    val templateParams =
      stats.getOrElse(Nil)
        .collect(Declaration.fromStat)
        .filter { d => isIncluded(d.mods) && !isExcluded(d.mods) }
        .map {
          case d@Declaration.Def(mods, name, decltpe, pos, _) =>
            applicableDef(d) match {
              case Right(_) => Param(mods, decltpe.get, name.get, named(mods))
              case Left(error) => abort(pos, error)
            }

          case d =>
            applicableDeclaration(d) match {
              case Right(_) =>
                d.name.fold(abort(d.pos, "Can not extract field name")) { name =>
                  Param(d.mods, d.decltpe.get, name, named(d.mods))
                }

              case Left(error) =>
                abort(d.pos, error)
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

  def isCaseClass(mods: Seq[Mod]): Boolean =
    mods exists {
      case Mod.Case() => true
      case _          => false
    }

  def annotationValue(mods: Seq[Mod], prefix: String, name: String): Option[Term] =
    mods collectFirst {
      case Mod.Annot(Term.Apply(Ctor.Ref.Select(Term.Name(`prefix`), Ctor.Ref.Name(`name`)), Seq(value: Term))) => value
    }

  private def applicableParam(param: Term.Param, constructorParamAccessibleByDefault: Boolean): Boolean =
    param.mods.forall(modifierMatcher) && (constructorParamAccessibleByDefault || param.mods.exists(valOrVarModifier))

  private def applicableDeclaration(declaration: Declaration): Either[String, Unit] = {
    declaration match {
      case d if d.decltpe.isEmpty =>
        Left(s"Member [${declaration.name.getOrElse("")}] type must be declared explicitly")

      case d if !d.mods.forall(modifierMatcher) =>
        Left(s"Member [${declaration.name.getOrElse("")}] has one of restricted modifiers: `private[this]` or `protected[this]`")

      case _ =>
        Right(())
    }
  }

  private def applicableDef(declaration: Declaration.Def): Either[String, Unit] = {
    declaration match {
      case d if d.decltpe.isEmpty =>
        Left(s"Member [${declaration.name.getOrElse("")}] type must be declared explicitly")

      case d if d.paramss.nonEmpty && d.paramss.head.nonEmpty =>
        Left(s"Member [${declaration.name.getOrElse("")}] must be declared without arguments")

      case d if !d.mods.forall(modifierMatcher) =>
        Left(s"Member [${declaration.name.getOrElse("")}] has one of restricted modifiers: `private[this]` or `protected[this]`")

      case _ =>
        Right(())
    }
  }

  private val modifierMatcher: Mod => Boolean = {
    case Mod.Private(_: Term.This)   => false
    case Mod.Protected(_: Term.This) => false
    case _                           => true
  }

  private val valOrVarModifier: Mod => Boolean = {
    case Mod.VarParam() => true
    case Mod.ValParam() => true
    case _              => false
  }

}
