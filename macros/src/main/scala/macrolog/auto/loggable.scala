package macrolog.auto

import macrolog.auto.Utils._
import org.scalameta.logger

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.collection.immutable.Seq
import scala.meta._

/**
 * @author Maksim Ochenashko
 */
@compileTimeOnly("loggable annotation should have been removed by compiler but was not")
class loggable extends StaticAnnotation {

  inline def apply(defn: Any): Any = meta {

    def generate(className: Type.Name, params: Seq[Param]): Seq[Stat] = {
      val imports: Seq[Import] = Seq(
        q"import _root_.macrolog.{LogSchema, Loggable}",
        q"import _root_.macrolog.Loggable._"
      )

      val logSchemaInstance: Defn.Val = {
        def operation(propertyType: Type, propName: Term.Name): Term.Tuple =
          q"(${Lit.String(propName.value)}, Loggable[$propertyType].print(value.$propName))"

        val chain: Seq[Term.Tuple] = params map { case Utils.Param(_, typ, name, _) => operation(typ, name) }

        q"""
            implicit val logSchemaInstance: LogSchema[$className] =
              new LogSchema[$className] {

                override def schema(value: $className): List[(String, String)] = {
                  List(..$chain)
                }

              }
        """
      }

      val loggableInstance: Defn.Val = {

        val mergeExpression =
          Term.ApplyInfix(
            Term.ApplyInfix(Term.Name("prop"), Term.Name("+"), Nil, Seq(Lit.String(" = "))),
            Term.Name("+"),
            Nil,
            Seq(Term.Name("v"))
          )

        val mkStringArgs = List(Lit.String("("), Lit.String(", "), Lit.String(")"))

        val classNameString = Lit.String(className.value)

        q"""
             implicit val loggableInstance: Loggable[$className] =
               Loggable.instance { value =>
                 val body = logSchemaInstance.schema(value)
                       .map { case (prop, v) => $mergeExpression }
                       .mkString(..$mkStringArgs)

                 $classNameString + body
               }
        """
      }

      imports :+ logSchemaInstance :+ loggableInstance
    }

    def generateCompanion(name: Type.Name, ctorParamss: Seq[Seq[Term.Param]], stats: Option[Seq[Stat]]): Defn.Object = {
      Defn.Object(
        Nil,
        Term.Name(name.value),
        Template(
          Nil, Nil, Term.Param(Nil, Name.Anonymous(), None, None),
          Some(generate(name, extractParams(defn, ctorParamss, stats)))
        )
      )
    }

    def trace(s: => String): Unit = {
      if (sys.props.get("macrolog.auto.trace").isDefined) logger.debug(s)
    }

    defn match {
      case traitDecl: Defn.Trait =>
        val companion = generateCompanion(traitDecl.name, traitDecl.ctor.paramss, traitDecl.templ.stats)

        trace(s"Generated companion class ${traitDecl.name.value}:\n" + companion)

        Term.Block(traitDecl :: companion :: Nil)

      case Term.Block((traitDecl: Defn.Trait) :: (compDecl: Defn.Object) :: Nil) =>
        val companion = compDecl.copy(
          templ = appendTemplate(
            compDecl.templ,
            generate(traitDecl.name, extractParams(defn, traitDecl.ctor.paramss, traitDecl.templ.stats))
          )
        )

        trace(s"Generated companion class ${traitDecl.name.value}:\n" + companion)

        Term.Block(traitDecl :: companion :: Nil)

      case classDecl: Defn.Class =>
        val companion = generateCompanion(classDecl.name, classDecl.ctor.paramss, classDecl.templ.stats)

        trace(s"Generated companion class ${classDecl.name.value}:\n" + companion)

        Term.Block(classDecl :: companion :: Nil)

      case Term.Block((classDecl: Defn.Class) :: (compDecl: Defn.Object) :: Nil) =>
        val companion = compDecl.copy(
          templ = appendTemplate(
            compDecl.templ,
            generate(classDecl.name, Utils.extractParams(defn, classDecl.ctor.paramss, classDecl.templ.stats))
          )
        )

        trace(s"Generated companion class ${classDecl.name.value}:\n" + companion)

        Term.Block(classDecl :: companion :: Nil)

      case _ =>
        abort(defn.pos, s"@loggable must annotate a case class")
    }

  }
}

object loggable {

  final class named(value: String) extends StaticAnnotation

  final class exclude extends StaticAnnotation

  final class include extends StaticAnnotation

}