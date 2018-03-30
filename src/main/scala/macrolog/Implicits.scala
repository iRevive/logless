package macrolog

import macrolog.TraceQualifier.DefinedTrace

/**
  * @author Maksim Ochenashko
  */
trait Implicits {

  @inline
  implicit def traceQualifierToLoggingContext(implicit traceQualifier: DefinedTrace): LoggingContext =
    new TraceQualifierLoggingContext.TraceQualifierLoggingContextImpl(traceQualifier)

}

object Implicits extends Implicits