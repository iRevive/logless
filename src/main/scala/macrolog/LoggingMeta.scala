package macrolog

import macrolog.auto.{NoPosition, Pos, Position}

/**
 * @author Maksim Ochenashko
 */
object LoggingMeta {

  private[macrolog] val metaStorage = new ThreadLocal[LoggingMetadata] {
    override def initialValue(): LoggingMetadata = LoggingMetadata(EmptyId, NoPosition)
  }

  def currentMeta: LoggingMetadata =
    metaStorage.get()

  def setCurrentMeta(context: LoggingMetadata): Unit =
    metaStorage.set(context)

  def clearCurrentMeta(): Unit =
    metaStorage.remove()

  def withMeta[T](code: => T)(implicit id: TraceQualifier, pos: Pos): T = {
    val oldMeta = metaStorage.get()
    metaStorage.set(LoggingMetadata(id, pos))

    try code finally metaStorage.set(oldMeta)
  }

}

case class LoggingMetadata(traceId: TraceQualifier, position: Position)

