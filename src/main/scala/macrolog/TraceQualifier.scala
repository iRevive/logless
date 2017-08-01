package macrolog

import java.util.UUID

/**
 * @author Maksim Ochenashko
 */
sealed trait TraceQualifier

case class TraceId(id: UUID = UUID.randomUUID()) extends TraceQualifier

case object EmptyId extends TraceQualifier

object TraceQualifier {

  implicit def generate: TraceQualifier = EmptyId

}