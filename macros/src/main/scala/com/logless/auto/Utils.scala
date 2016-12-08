package com.logless.auto

import scala.meta._
import scala.collection.immutable.Seq

/**
  * @author Maksim Ochenashko
  */
private[auto] object Utils {

  private[auto] def isAnnotated(mods: Seq[Mod], name: String): Boolean =
    mods exists {
      case Mod.Annot(Ctor.Ref.Name(`name`)) => true
      case _                                => false
    }

  private[auto] def extractParamType(param: Term.Param): Type =
    param.decltpe match {
      case Some(t: Type)  => t
      case _              => abort(param.pos, "Field without type declaration")
    }

  private[auto] def extractName(pats: Seq[Pat]): Option[Term.Name] =
    pats collectFirst { case Pat.Var.Term(name) => name }

  private[auto] def applicableParam(param: Term.Param): Boolean =
    param.mods forall modifierMatcher

  private[auto] def appendTemplate(template: Template, stat: Stat): Template =
    appendTemplate(template, stat :: Nil)

  private[auto] def appendTemplate(template: Template, stats: List[Stat]): Template = {
    val _stats = template.stats match {
      case Some(body) => Some(body ++ stats)
      case None       => Some(stats)
    }

    template.copy(stats = _stats)
  }

  private[auto] def applicableDeclaration(v: Declaration): Boolean =
    v.decltpe.nonEmpty && v.mods.forall(modifierMatcher)

  private[auto] def applicableDef(v: Declaration.Def): Boolean =
    v.decltpe.nonEmpty && (v.paramss.isEmpty || v.paramss.head.isEmpty) && v.mods.forall(modifierMatcher)

  private[auto] def elementExists(body: Seq[Stat], termName: String): Boolean =
    body exists {
      case q"val ..$prop: $_" if prop.exists(_.name.value == termName)  => true
      case q"def $name[..$_](...$_): $_" if name.value == termName      => true
      case _                                                            => false
    }

  private[this] val modifierMatcher: Mod => Boolean = {
    case Mod.Private(_: Term.This)    => false
    case Mod.Protected(_: Term.This)  => false
    case _                            => true
  }

}