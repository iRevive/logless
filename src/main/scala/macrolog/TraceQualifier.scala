package macrolog

import java.util.UUID

/**
  * @author Maksim Ochenashko
  */
sealed trait TraceQualifier

object TraceQualifier {

  trait DefinedTrace extends TraceQualifier {

    def asString: String

  }

  case object NoTraceQualifier extends TraceQualifier

  final case class TraceId(id: UUID = UUID.randomUUID()) extends DefinedTrace {

    override def asString: String = id.toString

  }

}