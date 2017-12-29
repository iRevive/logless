package macrolog.auto

import scala.annotation.tailrec
import scala.language.experimental.macros

/**
  * Inspired by com.lihaoyi.Sourcecode
  *
  * @author Maksim Ochenashko
  */
case class Position(enclosingClass: String, enclosingMethod: Option[String], fullPosition: String)

object Position {

  implicit def generate: Position = macro impl

  def impl(c: scala.reflect.macros.blackbox.Context): c.Expr[Position] = {
    import c.universe._

    val owner = c.internal.enclosingOwner

    @tailrec
    def extract(s: Symbol)(p: Symbol => Boolean): Option[Symbol] =
      if (s == null || s == NoSymbol) None
      else if (p(s)) Some(s)
      else extract(s.owner)(p)

    val enclosingClass = extract(owner) { s =>
      (s.name.decodedName.toString != "$anonfun") &&
        (s.name.decodedName.toString != "$anon") &&
        (s.isClass || s.isModuleClass || s.isModule || s.isPackage || s.isPackageClass)
    }

    if (enclosingClass.isEmpty) c.abort(c.enclosingPosition, "Can not detect enclosing element (class, object, package)")

    val enclosingMethod = extract(owner) { s =>
      s.isMethod && s.name.decodedName.toString != "applyOrElse"
    }

    val className = enclosingClass.get.name.toString.trim

    val methodName = enclosingMethod.map(_.name.toString)

    val fullName = methodName match {
      case Some(m) => enclosingClass.get.fullName + "." + m + ":" + owner.pos.line
      case None    => enclosingClass.get.fullName + ":" + owner.pos.line
    }

    c.Expr[macrolog.auto.Position](q"""${c.prefix}($className, $methodName, $fullName)""")
  }

}