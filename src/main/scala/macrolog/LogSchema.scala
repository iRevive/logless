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

  def schema(value: A): List[(String, String)]

}