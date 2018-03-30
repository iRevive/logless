package macrolog

import macrolog.TraceQualifier.DefinedTrace

/**
  * @author Maksim Ochenashko
  */
trait Implicits {

  @inline
  implicit def traceQualifierToLoggingContext(traceQualifier: DefinedTrace): LoggingContext =
    new TraceQualifierLoggingContext.TraceQualifierLoggingContextImpl(traceQualifier)

  @inline
  implicit def implTraceQualifierToLoggingContext(implicit traceQualifier: DefinedTrace): LoggingContext =
    new TraceQualifierLoggingContext.TraceQualifierLoggingContextImpl(traceQualifier)

}

object Implicits extends Implicits