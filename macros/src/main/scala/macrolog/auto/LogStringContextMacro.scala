package macrolog.auto

import scala.reflect.macros._

/**
 * @author Maksim Ochenashko
 */
object LogStringContextMacro {

  def logImpl(c: whitebox.Context)(args: c.Expr[Any]*): c.Expr[String] = {
    import c.universe._

    val scParts = c.prefix.tree
      .collect {
        case Apply(Select(Select(Ident(TermName("scala")), TermName("StringContext")), TermName("apply")), parts) => parts
      }
      .headOption
      .toList
      .flatten

    val loggableArgs = args map { arg =>
      q"Loggable[${arg.actualType}].print(${arg.tree})"
    }

    val result =
      q"""
          {
            import _root_.macrolog.Loggable
            StringContext(..$scParts).s(..$loggableArgs)
          }
       """

    c.Expr[String](result)
  }

}
