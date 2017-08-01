package macrolog

import com.typesafe.scalalogging.{Logger => ScalaLogger}
import macrolog.auto.Pos
import org.slf4j.{Logger => Underlying}

import scala.reflect.ClassTag

/**
 * @author Maksim Ochenashko
 */
object Logger {

  def apply(scalaLogger: ScalaLogger): Logger =
    new Logger(scalaLogger)

  def apply(underlying: Underlying): Logger =
    new Logger(ScalaLogger(underlying))

  def apply(name: String): Logger =
    new Logger(ScalaLogger(name))

  def apply(clazz: Class[_]): Logger =
    new Logger(ScalaLogger(clazz))

  def apply[T: ClassTag]: Logger =
    new Logger(ScalaLogger[T])

}

@SerialVersionUID(716196318)
final class Logger private(val underlying: ScalaLogger) extends Serializable {

  // Error
  def error(message: String)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    LoggingMeta.withMeta(underlying.error(message))

  def error(message: String, cause: Throwable)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    LoggingMeta.withMeta(underlying.error(message, cause))

  // Warn

  def warn(message: String)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    LoggingMeta.withMeta(underlying.warn(message))

  def warn(message: String, cause: Throwable)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    LoggingMeta.withMeta(underlying.warn(message, cause))

  // Info

  def info(message: String)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    LoggingMeta.withMeta(underlying.info(message))

  def info(message: String, cause: Throwable)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    LoggingMeta.withMeta(underlying.info(message, cause))

  // Debug

  def debug(message: String)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    LoggingMeta.withMeta(underlying.debug(message))

  def debug(message: String, cause: Throwable)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    LoggingMeta.withMeta(underlying.debug(message, cause))

  // Trace

  def trace(message: String)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    LoggingMeta.withMeta(underlying.trace(message))

  def trace(message: String, cause: Throwable)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    LoggingMeta.withMeta(underlying.trace(message, cause))

}