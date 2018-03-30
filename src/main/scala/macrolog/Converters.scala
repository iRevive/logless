package macrolog

import ch.qos.logback.classic.pattern._
import ch.qos.logback.classic.spi.ILoggingEvent

/**
  * @author Maksim Ochenashko
  */
class TraceQualifierConverter extends ClassicConverter {

  def convert(event: ILoggingEvent): String =
    Option(event.getArgumentArray).flatMap(_.headOption) match {
      case Some(PositionLoggingContext(TraceQualifierLoggingContext(trace), _)) => trace.asString
      case _                                                                    => "undefined"
    }

}

class PositionConverter extends NamedConverter {

  protected def getFullyQualifiedName(event: ILoggingEvent): String =
    Option(event.getArgumentArray).flatMap(_.headOption) match {
      case Some(PositionLoggingContext(_, pos)) => pos.fullPosition
      case _                                    => "undefined"
    }

}