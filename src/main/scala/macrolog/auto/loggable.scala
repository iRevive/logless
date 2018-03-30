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
    defn match {
      case traitDefn: Defn.Trait =>
        LoggableMacroImpl.expandTrait(traitDefn)

      case Term.Block((traitDefn: Defn.Trait) :: (compDefn: Defn.Object) :: Nil) =>
        LoggableMacroImpl.expandTrait(traitDefn, compDefn)

      case classDefn: Defn.Class =>
        LoggableMacroImpl.expandClass(classDefn)

      case Term.Block((classDefn: Defn.Class) :: (compDefn: Defn.Object) :: Nil) =>
        LoggableMacroImpl.expandClass(classDefn, compDefn)

      case _ =>
        abort(defn.pos, s"@loggable must annotate a class or trait")
    }
  }

}

object loggable {

  final class named(value: String) extends StaticAnnotation

  final class exclude extends StaticAnnotation

  final class include extends StaticAnnotation

}

object LoggableMacroImpl {

  def expandTrait(traitDefn: Defn.Trait): Term.Block = {
    val companion = generateCompanion(traitDefn, traitDefn.name, traitDefn.ctor.paramss, traitDefn.templ.stats)

    trace(s"Generated companion class ${traitDefn.name.value}:\n" + companion)

    Term.Block(traitDefn :: companion :: Nil)
  }

  def expandTrait(traitDefn: Defn.Trait, compDefn: Defn.Object): Term.Block = {
    val companion = compDefn.copy(
      templ = appendTemplate(
        compDefn.templ,
        generateBody(traitDefn.name, extractParams(traitDefn, traitDefn.ctor.paramss, traitDefn.templ.stats))
      )
    )

    trace(s"Generated companion class ${traitDefn.name.value}:\n" + companion)

    Term.Block(traitDefn :: companion :: Nil)
  }

  def expandClass(classDefn: Defn.Class): Term.Block = {
    val companion = generateCompanion(classDefn, classDefn.name, classDefn.ctor.paramss, classDefn.templ.stats)

    trace(s"Generated companion class ${classDefn.name.value}:\n" + companion)

    Term.Block(classDefn :: companion :: Nil)
  }

  def expandClass(classDefn: Defn.Class, compDefn: Defn.Object): Term.Block = {
    val companion = compDefn.copy(
      templ = appendTemplate(
        compDefn.templ,
        generateBody(classDefn.name, Utils.extractParams(classDefn, classDefn.ctor.paramss, classDefn.templ.stats))
      )
    )

    trace(s"Generated companion class ${classDefn.name.value}:\n" + companion)

    Term.Block(classDefn :: companion :: Nil)
  }

  def generateCompanion(defn: Stat,
                        name: Type.Name,
                        ctorParamss: Seq[Seq[Term.Param]],
                        stats: Option[Seq[Stat]]): Defn.Object = {
    Defn.Object(
      Nil,
      Term.Name(name.value),
      Template(
        Nil, Nil, Term.Param(Nil, Name.Anonymous(), None, None),
        Some(generateBody(name, extractParams(defn, ctorParamss, stats)))
      )
    )
  }

  def generateBody(className: Type.Name, params: Seq[Param]): Seq[Stat] = {
    val imports: Seq[Import] = Seq(
      q"import _root_.macrolog.{LogSchema, Loggable}",
      q"import _root_.macrolog.Loggable._"
    )

    val logSchema: Defn.Val = logSchemaInstance(className, params)

    val loggable: Defn.Val = loggableInstance(className)

    imports :+ logSchema :+ loggable
  }

  def logSchemaInstance(className: Type.Name, params: Seq[Param]): Defn.Val = {
    def operation(propertyType: Type, propName: Term.Name): Term.Tuple =
      q"(${Lit.String(propName.value)}, Loggable[$propertyType].print(value.$propName))"

    val chain: Seq[Term.Tuple] = params map { case Utils.Param(_, typ, name, _) => operation(typ, name) }

    q"""
        implicit val logSchemaInstance: LogSchema[$className] =
          new LogSchema[$className] {

            override def schema(value: $className): Map[String, String] = {
              Map(..$chain)
            }

          }
    """
  }

  def loggableInstance(className: Type.Name): Defn.Val = {

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

  def trace(s: => String): Unit = {
    if (sys.props.get("macrolog.auto.trace").isDefined) logger.debug(s)
  }

}