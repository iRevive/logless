package macrolog.auto

import scala.collection.immutable.Seq
import scala.meta._

/**
  * @author Maksim Ochenashko
  */
private[auto] sealed trait Declaration {
  def mods: Seq[Mod]
  def name: Option[Term.Name]
  def decltpe: Option[Type]
  def pos: scala.meta.inputs.Position
}

private[auto] object Declaration {

  case class Val(mods: Seq[Mod], name: Option[Term.Name], decltpe: Option[Type], pos: scala.meta.inputs.Position) extends Declaration
  case class Var(mods: Seq[Mod], name: Option[Term.Name], decltpe: Option[Type], pos: scala.meta.inputs.Position) extends Declaration
  case class Def(mods: Seq[Mod], name: Option[Term.Name], decltpe: Option[Type], pos: scala.meta.inputs.Position, paramss: Seq[Seq[Term.Param]]) extends Declaration

  val fromStat: PartialFunction[Stat, Declaration] = {
    case v: Decl.Val => Val(v.mods, extractName(v.pats), Some(v.decltpe), v.pos)
    case v: Defn.Val => Val(v.mods, extractName(v.pats), v.decltpe, v.pos)

    case v: Decl.Var => Var(v.mods, extractName(v.pats), Some(v.decltpe), v.pos)
    case v: Defn.Var => Var(v.mods, extractName(v.pats), v.decltpe, v.pos)

    case v: Decl.Def => Def(v.mods, Some(v.name), Some(v.decltpe), v.pos, v.paramss)
    case v: Defn.Def => Def(v.mods, Some(v.name), v.decltpe, v.pos, v.paramss)
  }

  private def extractName(pats: Seq[Pat]): Option[Term.Name] =
    pats collectFirst { case Pat.Var.Term(name) => name }
}