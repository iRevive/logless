package macrolog

import com.typesafe.scalalogging.{Logger => ScalaLogger}
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
final class Logger private[macrolog](val underlying: ScalaLogger) extends Serializable {

  // Error
  def error(message: => String)(implicit ctx: LoggingContext, pos: Position): Unit =
    underlying.error(message, ctx.withPosition(pos))

  def error(message: => String, cause: => Throwable)(implicit ctx: LoggingContext, pos: Position): Unit =
    underlying.error(message, ctx.withPosition(pos), cause)

  // Warn

  def warn(message: => String)(implicit ctx: LoggingContext, pos: Position): Unit =
    underlying.warn(message, ctx.withPosition(pos))

  def warn(message: => String, cause: => Throwable)(implicit ctx: LoggingContext, pos: Position): Unit =
    underlying.warn(message, ctx.withPosition(pos), cause)

  // Info

  def info(message: => String)(implicit ctx: LoggingContext, pos: Position): Unit =
    underlying.info(message, ctx.withPosition(pos))

  def info(message: => String, cause: => Throwable)(implicit ctx: LoggingContext, pos: Position): Unit =
    underlying.info(message, ctx.withPosition(pos), cause)

  // Debug

  def debug(message: => String)(implicit ctx: LoggingContext, pos: Position): Unit =
    underlying.debug(message, ctx.withPosition(pos))

  def debug(message: => String, cause: => Throwable)(implicit ctx: LoggingContext, pos: Position): Unit =
    underlying.debug(message, ctx.withPosition(pos), cause)

  // Trace

  def trace(message: => String)(implicit ctx: LoggingContext, pos: Position): Unit =
    underlying.trace(message, ctx.withPosition(pos))

  def trace(message: => String, cause: => Throwable)(implicit ctx: LoggingContext, pos: Position): Unit =
    underlying.trace(message, ctx.withPosition(pos), cause)

}