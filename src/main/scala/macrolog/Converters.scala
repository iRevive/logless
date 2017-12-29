package macrolog

import ch.qos.logback.classic.pattern._
import ch.qos.logback.classic.spi.ILoggingEvent

/**
 * @author Maksim Ochenashko
 */
class TraceQualifierConverter extends ClassicConverter {

  def convert(event: ILoggingEvent): String =
    event.getArgumentArray.headOption match {
      case Some(TraceQualifierLoggingContext(trace)) => trace.asString
      case _                                         => "undefined"
    }

}

class PositionConverter extends NamedConverter {

  protected def getFullyQualifiedName(event: ILoggingEvent): String =
    event.getArgumentArray.headOption match {
      case Some(PositionLoggingContext(pos)) => pos.fullPosition
      case _                                 => "undefined"
    }

}