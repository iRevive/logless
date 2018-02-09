package macrolog

import macrolog.TraceQualifier.DefinedTrace

import scala.language.implicitConversions

/**
  * @author Maksim Ochenashko
  */
trait LowPriority {

  @inline
  implicit def positionToLoggingContext(implicit pos: Position): LoggingContext =
    new PositionLoggingContext.PositionLoggingContextImpl(pos)

}

trait HighPriority extends LowPriority {

  @inline
  implicit def traceQualifierToLoggingContext(implicit traceQualifier: DefinedTrace, pos: Position): LoggingContext =
    new TraceQualifierLoggingContext.TraceQualifierLoggingContextImpl(traceQualifier, pos)

}

object Implicits extends HighPriority