package com.logless

import com.logless.builder.LogBuilder
import com.typesafe.scalalogging.{Logger => ScalaLogger}
import org.slf4j.{Marker, Logger => Underlying}

import scala.reflect.ClassTag

/**
  * @author Maksim Ochenashko
  */
object TraceLogger {

  def apply(scalaLogger: ScalaLogger): TraceLogger =
    new TraceLogger(scalaLogger)

  def apply(underlying: Underlying): TraceLogger =
    new TraceLogger(ScalaLogger(underlying))

  def apply(name: String): TraceLogger =
    new TraceLogger(ScalaLogger(name))

  def apply(clazz: Class[_]): TraceLogger =
    new TraceLogger(ScalaLogger(clazz))

  def apply[T](showSource: Boolean)(implicit ct: ClassTag[T]): TraceLogger =
    new TraceLogger(ScalaLogger(ct))
  
}

@SerialVersionUID(716196318)
final class TraceLogger private(val underlying: ScalaLogger) extends Serializable {

  // Error

  def error(message: String)(implicit tracer: TraceIdentifier): Unit =
    underlying.error(withTrace(message))

  def error(message: String, cause: Throwable)(implicit tracer: TraceIdentifier): Unit =
    underlying.error(withTrace(message), cause)

  def error(message: String, builder: LogBuilder)(implicit tracer: TraceIdentifier): Unit =
    underlying.error(withTrace(message), builder)

  def error(message: String, args: Any*)(implicit tracer: TraceIdentifier): Unit =
    underlying.error(withTrace(message), args)

  def error(marker: Marker, message: String)(implicit tracer: TraceIdentifier): Unit =
    underlying.error(marker, withTrace(message))

  def error(marker: Marker, message: String, cause: Throwable)(implicit tracer: TraceIdentifier): Unit =
    underlying.error(marker, withTrace(message), cause)

  def error(marker: Marker, message: String, args: Any*)(implicit tracer: TraceIdentifier): Unit =
    underlying.error(marker, withTrace(message), args)

  // Warn

  def warn(message: String)(implicit tracer: TraceIdentifier): Unit =
    underlying.warn(withTrace(message))

  def warn(message: String, cause: Throwable)(implicit tracer: TraceIdentifier): Unit =
    underlying.warn(withTrace(message), cause)

  def warn(message: String, builder: LogBuilder)(implicit tracer: TraceIdentifier): Unit =
    underlying.warn(withTrace(message), builder)

  def warn(message: String, args: Any*)(implicit tracer: TraceIdentifier): Unit =
    underlying.warn(withTrace(message), args)

  def warn(marker: Marker, message: String)(implicit tracer: TraceIdentifier): Unit =
    underlying.warn(marker, withTrace(message))

  def warn(marker: Marker, message: String, cause: Throwable)(implicit tracer: TraceIdentifier): Unit =
    underlying.warn(marker, withTrace(message), cause)

  def warn(marker: Marker, message: String, args: Any*)(implicit tracer: TraceIdentifier): Unit =
    underlying.warn(marker, withTrace(message), args)

  // Info

  def info(message: String)(implicit tracer: TraceIdentifier): Unit =
    underlying.info(withTrace(message))

  def info(message: String, cause: Throwable)(implicit tracer: TraceIdentifier): Unit =
    underlying.info(withTrace(message), cause)

  def info(message: String, builder: LogBuilder)(implicit tracer: TraceIdentifier): Unit =
    underlying.info(withTrace(message), builder)

  def info(message: String, args: Any*)(implicit tracer: TraceIdentifier): Unit =
    underlying.info(withTrace(message), args)

  def info(marker: Marker, message: String)(implicit tracer: TraceIdentifier): Unit =
    underlying.info(marker, withTrace(message))

  def info(marker: Marker, message: String, cause: Throwable)(implicit tracer: TraceIdentifier): Unit =
    underlying.info(marker, withTrace(message), cause)

  def info(marker: Marker, message: String, args: Any*)(implicit tracer: TraceIdentifier): Unit =
    underlying.info(marker, withTrace(message), args)

  // Debug

  def debug(message: String)(implicit tracer: TraceIdentifier): Unit =
    underlying.debug(withTrace(message))

  def debug(message: String, cause: Throwable)(implicit tracer: TraceIdentifier): Unit =
    underlying.debug(withTrace(message), cause)

  def debug(message: String, builder: LogBuilder)(implicit tracer: TraceIdentifier): Unit =
    underlying.debug(withTrace(message), builder)

  def debug(message: String, args: Any*)(implicit tracer: TraceIdentifier): Unit =
    underlying.debug(withTrace(message), args)

  def debug(marker: Marker, message: String)(implicit tracer: TraceIdentifier): Unit =
    underlying.debug(withTrace(message), message)

  def debug(marker: Marker, message: String, cause: Throwable)(implicit tracer: TraceIdentifier): Unit =
    underlying.debug(withTrace(message), message, cause)

  def debug(marker: Marker, message: String, args: Any*)(implicit tracer: TraceIdentifier): Unit =
    underlying.debug(withTrace(message), message, args)

  // Trace

  def trace(message: String)(implicit tracer: TraceIdentifier): Unit =
    underlying.trace(withTrace(message))

  def trace(message: String, cause: Throwable)(implicit tracer: TraceIdentifier): Unit =
    underlying.trace(withTrace(message), cause)

  def trace(message: String, builder: LogBuilder)(implicit tracer: TraceIdentifier): Unit =
    underlying.trace(withTrace(message), builder)

  def trace(message: String, args: Any*)(implicit tracer: TraceIdentifier): Unit =
    underlying.trace(withTrace(message), args)

  def trace(marker: Marker, message: String)(implicit tracer: TraceIdentifier): Unit =
    underlying.trace(marker, withTrace(message))

  def trace(marker: Marker, message: String, cause: Throwable)(implicit tracer: TraceIdentifier): Unit =
    underlying.trace(marker, withTrace(message), cause)

  def trace(marker: Marker, message: String, args: Any*)(implicit tracer: TraceIdentifier): Unit =
    underlying.trace(marker, withTrace(message), args)

  @inline
  private[logless] def withTrace(other: String)(implicit tracer: TraceIdentifier): String =
    tracer match {
      case TraceID(id) => s"[$id]: $other"
      case DummyID     => other
    }

}