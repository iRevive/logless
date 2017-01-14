package com.logless.source

import scala.annotation.tailrec
import scala.language.experimental.macros

/**
  * @author Maksim Ochenashko
  */
case class Source(enclosingClass: String, enclosingMethod: Option[String])

object Source {

  implicit def generate: Source = macro impl

  def impl(c: scala.reflect.macros.blackbox.Context): c.Expr[Source] = {
    import c.universe._

    val owner = c.internal.enclosingOwner

    @tailrec
    def extract(s: Symbol)(p: Symbol => Boolean): Option[Symbol] =
      if (s == null || s == NoSymbol) None
      else if (p(s)) Some(s)
      else extract(s.owner)(p)

    val enclosingClass = extract(owner) { s =>
      (s.name.decodedName.toString != "$anonfun") &&
        (s.isClass || s.isModuleClass || s.isModule || s.isPackage || s.isPackageClass)
    }

    if (enclosingClass.isEmpty) c.abort(c.enclosingPosition, "Can not detect enclosing element (class, object, package)")

    val enclosingMethod = extract(owner)(_.isMethod)

    val className = enclosingClass.get.name.toString.trim

    val methodName = enclosingMethod.map(_.name.toString)

    c.Expr[Source](q"""${c.prefix}($className, $methodName)""")
  }

}