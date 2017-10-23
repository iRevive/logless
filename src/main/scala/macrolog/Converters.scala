package macrolog

import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import macrolog.auto.{Pos, Position}

/**
 * @author Maksim Ochenashko
 */
class TraceQualifierConverter extends ClassicConverter {

  def convert(event: ILoggingEvent): String =
    event.getArgumentArray.headOption match {
      case Some(LoggingMetadata(TraceId(id), _)) => id.toString
      case _                                     => "undefined"
    }

}

class PositionConverter extends ClassicConverter {

  def convert(event: ILoggingEvent): String =
    event.getArgumentArray.headOption match {
      case Some(LoggingMetadata(_, pos: Pos)) => pos.fullPosition
      case _                                  => "undefined"
    }

}

case class LoggingMetadata(traceId: TraceQualifier, position: Position)