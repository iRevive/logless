package macrolog

import macrolog.TraceQualifier.DefinedTrace

/**
  * @author Maksim Ochenashko
  */
trait LoggingContext {

  def withPosition(position: Position): PositionLoggingContext

}

trait PositionLoggingContext extends LoggingContext {

  def position: Position

}

object PositionLoggingContext {

  def unapply(arg: PositionLoggingContext): Option[Position] =
    Some(arg.position)

  class PositionLoggingContextImpl(val position: Position) extends PositionLoggingContext {

    override def withPosition(position: Position): PositionLoggingContextImpl =
      new PositionLoggingContextImpl(position)

  }

}

trait TraceQualifierLoggingContext extends LoggingContext {

  def traceQualifier: DefinedTrace

}

object TraceQualifierLoggingContext {

  def unapply(arg: TraceQualifierLoggingContext): Option[DefinedTrace] =
    Some(arg.traceQualifier)

  class TraceQualifierLoggingContextImpl(val traceQualifier: DefinedTrace, val position: Position)
    extends TraceQualifierLoggingContext
      with PositionLoggingContext {

    override def withPosition(position: Position): TraceQualifierLoggingContextImpl =
      new TraceQualifierLoggingContextImpl(traceQualifier, position)

  }

}