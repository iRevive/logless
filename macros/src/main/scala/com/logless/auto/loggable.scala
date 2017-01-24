package com.logless.auto

import scala.annotation.meta.getter
import scala.annotation.StaticAnnotation
import scala.collection.immutable.Seq
import scala.meta._

/**
  * @author Maksim Ochenashko
  */
final class loggable extends StaticAnnotation {

  inline def apply(defn: Any): Any = meta {
    def loggable(className: Type.Name, template: Template, ctorParams: Seq[Seq[Term.Param]]): Defn.Val = {
      val fields: Seq[(Type, Term.Name)] = template.stats.getOrElse(Nil)
        .collect(Declaration.fromStat)
        .filter { d => Utils.isAnnotated(d.mods, "include") }
        .map {
          case d@Declaration.Def(_, name, decltpe, pos, _) =>
            if (Utils.applicableDef(d)) (decltpe.get, name.get)
            else abort(pos, "`private[this]` and `protected[this]` is not allowed. Def type must be declared explicitly")

          case d =>
            if (Utils.applicableDeclaration(d)) d.name.fold(abort(d.pos, "Can not extract field name")) { name => (d.decltpe.get, name) }
            else abort(d.pos, s"`private[this]` and `protected[this]` is not allowed. Variable type must be declared explicitly")

        }

      val params: Seq[(Type, Term.Name)] = ctorParams.headOption.getOrElse(Nil)
        .filter { param => Utils.applicableParam(param) && !Utils.isAnnotated(param.mods, "exclude") }
        .map { param => (Utils.extractParamType(param), Term.Name(param.name.value)) }

      val builder = params ++ fields match {
        case Nil =>
          Lit("")

        case (typ, name) :: Nil =>
          q"_root_.com.logless.builder.Loggable[$typ].present(value.$name)"

        case other =>
          val chain = other
            .map { case (_, name) => q"value.$name" }
            .reduceRight { (left: Term, right: Term) => q"$left :+: $right" }

          q"($chain).print"
      }

      val replace =
        Term.Apply(
          Term.Select(
            Lit(Config.format.replace("$className", className.value)),
            Term.Name("replace")
          ),
          Seq(Lit("$body"), builder)
        )

      q"""
          implicit val loggable: _root_.com.logless.builder.Loggable[$className] =
            _root_.com.logless.builder.Loggable.presents[$className] { value =>
              import _root_.com.logless._

              $replace
          }
      """
    }

    defn match {
      case traitDecl: Defn.Trait =>
        val companion = q"object ${Term.Name(traitDecl.name.value)} { ${loggable(traitDecl.name, traitDecl.templ, traitDecl.ctor.paramss)} }"
        Term.Block(traitDecl :: companion :: Nil)

      case Term.Block((traitDecl: Defn.Trait) :: (compDecl: Defn.Object) :: Nil) =>
        val companion = compDecl.copy(templ = Utils.appendTemplate(compDecl.templ, loggable(traitDecl.name, traitDecl.templ, traitDecl.ctor.paramss)))
        Term.Block(traitDecl :: companion :: Nil)

      case classDecl: Defn.Class =>
        val companion = q"object ${Term.Name(classDecl.name.value)} { ${loggable(classDecl.name, classDecl.templ, classDecl.ctor.paramss)} }"
        Term.Block(classDecl :: companion :: Nil)

      case Term.Block((classDecl: Defn.Class) :: (compDecl: Defn.Object) :: Nil) =>
        val companion = compDecl.copy(templ = Utils.appendTemplate(compDecl.templ, loggable(classDecl.name, classDecl.templ, classDecl.ctor.paramss)))
        Term.Block(classDecl :: companion :: Nil)

      case _ =>
        abort(defn.pos, "Only case classes & traits supported")
    }
  }

}

@getter
final class include extends StaticAnnotation

final class exclude extends StaticAnnotation