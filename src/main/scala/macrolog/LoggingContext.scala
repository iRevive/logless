package macrolog

import macrolog.TraceQualifier.DefinedTrace

/**
  * @author Maksim Ochenashko
  */
trait LoggingContext

trait TraceQualifierLoggingContext extends LoggingContext {

  def traceQualifier: DefinedTrace

}

object TraceQualifierLoggingContext {

  def unapply(arg: TraceQualifierLoggingContext): Option[DefinedTrace] =
    Some(arg.traceQualifier)

  class TraceQualifierLoggingContextImpl(val traceQualifier: DefinedTrace) extends TraceQualifierLoggingContext

}

final case class PositionLoggingContext(ctx: LoggingContext, position: Position)