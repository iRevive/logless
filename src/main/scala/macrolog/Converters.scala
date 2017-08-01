package macrolog

import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import macrolog.auto.Pos

/**
 * @author Maksim Ochenashko
 */
class TraceQualifierConverter extends ClassicConverter {

  def convert(event: ILoggingEvent): String =
    LoggingMeta.currentMeta match {
      case LoggingMetadata(TraceId(id), _) => id.toString
      case _                               => "undefined"
    }

}

class PositionConverter extends ClassicConverter {

  override def convert(event: ILoggingEvent): String =
    LoggingMeta.currentMeta match {
      case LoggingMetadata(_, pos: Pos) => pos.fullPosition
      case _                            => "undefined"
    }

}