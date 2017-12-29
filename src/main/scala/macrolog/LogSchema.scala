package macrolog

import simulacrum.typeclass

import scala.annotation.implicitNotFound
import scala.language.implicitConversions

/**
  * @author Maksim Ochenashko
  */
@implicitNotFound(
  """
 No LogSchema found for type ${A}. Try to implement an implicit LogSchema[${A}].
 You can implement it in ${A} companion class.
    """
)
@typeclass
trait LogSchema[A] {

  def schema(value: A): Map[String, String]

}

object LogSchema {

  def instance[A](op: A => Map[String, String]): LogSchema[A] = new LogSchema[A] {
    override def schema(value: A): Map[String, String] = op(value)
  }

}