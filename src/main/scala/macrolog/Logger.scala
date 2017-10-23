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
  def error(message: => String)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    underlying.error(message, LoggingMetadata(tracer, pos))

  def error(message: => String, cause: => Throwable)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    underlying.error(message, LoggingMetadata(tracer, pos), cause)

  // Warn

  def warn(message: => String)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    underlying.warn(message, LoggingMetadata(tracer, pos))

  def warn(message: => String, cause: => Throwable)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    underlying.warn(message, LoggingMetadata(tracer, pos), cause)

  // Info

  def info(message: => String)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    underlying.info(message, LoggingMetadata(tracer, pos))

  def info(message: => String, cause: => Throwable)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    underlying.info(message, LoggingMetadata(tracer, pos), cause)

  // Debug

  def debug(message: => String)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    underlying.debug(message, LoggingMetadata(tracer, pos))

  def debug(message: => String, cause: => Throwable)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    underlying.debug(message, LoggingMetadata(tracer, pos), cause)

  // Trace

  def trace(message: => String)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    underlying.trace(message, LoggingMetadata(tracer, pos))

  def trace(message: => String, cause: => Throwable)(implicit tracer: TraceQualifier, pos: Pos): Unit =
    underlying.trace(message, LoggingMetadata(tracer, pos), cause)

}