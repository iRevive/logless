package com.logless

import com.logless.source.Source
import org.slf4j.{Marker, Logger => Underlying}
import com.typesafe.scalalogging.{Logger => ScalaLogger}

import scala.reflect.ClassTag

/**
  * @author Maksim Ochenashko
  */
object Logger {

  def apply(scalaLogger: ScalaLogger, showSource: Boolean): Logger =
    new Logger(scalaLogger, showSource)

  def apply(underlying: Underlying, showSource: Boolean): Logger =
    new Logger(ScalaLogger(underlying), showSource)

  def apply(name: String, showSource: Boolean): Logger =
    new Logger(ScalaLogger(name), showSource)

  def apply(clazz: Class[_], showSource: Boolean): Logger =
    new Logger(ScalaLogger(clazz), showSource)

  def apply[T](showSource: Boolean)(implicit ct: ClassTag[T]): Logger =
    new Logger(ScalaLogger(ct), showSource)

}

@SerialVersionUID(716196318)
final class Logger private(val underlying: ScalaLogger, val showSource: Boolean) extends Serializable {

  // Error

  def error(message: String)(implicit source: Source): Unit =
    underlying.error(withSource(message))

  def error(message: String, cause: Throwable)(implicit source: Source): Unit =
    underlying.error(withSource(message), cause)

  def error(message: String, builder: LogBuilder)(implicit source: Source): Unit =
    underlying.error(withSource(message), builder)

  def error(message: String, args: Any*)(implicit source: Source): Unit =
    underlying.error(withSource(message), args)

  def error(marker: Marker, message: String)(implicit source: Source): Unit =
    underlying.error(marker, withSource(message))

  def error(marker: Marker, message: String, cause: Throwable)(implicit source: Source): Unit =
    underlying.error(marker, withSource(message), cause)

  def error(marker: Marker, message: String, args: Any*)(implicit source: Source): Unit =
    underlying.error(marker, withSource(message), args)

  // Warn

  def warn(message: String)(implicit source: Source): Unit =
    underlying.warn(withSource(message))

  def warn(message: String, cause: Throwable)(implicit source: Source): Unit =
    underlying.warn(withSource(message), cause)

  def warn(message: String, builder: LogBuilder)(implicit source: Source): Unit =
    underlying.warn(withSource(message), builder)

  def warn(message: String, args: Any*)(implicit source: Source): Unit =
    underlying.warn(withSource(message), args)

  def warn(marker: Marker, message: String)(implicit source: Source): Unit =
    underlying.warn(marker, withSource(message))

  def warn(marker: Marker, message: String, cause: Throwable)(implicit source: Source): Unit =
    underlying.warn(marker, withSource(message), cause)

  def warn(marker: Marker, message: String, args: Any*)(implicit source: Source): Unit =
    underlying.warn(marker, withSource(message), args)

  // Info

  def info(message: String)(implicit source: Source): Unit =
    underlying.info(withSource(message))

  def info(message: String, cause: Throwable)(implicit source: Source): Unit =
    underlying.info(withSource(message), cause)

  def info(message: String, builder: LogBuilder)(implicit source: Source): Unit =
    underlying.info(withSource(message), builder)

  def info(message: String, args: Any*)(implicit source: Source): Unit =
    underlying.info(withSource(message), args)

  def info(marker: Marker, message: String)(implicit source: Source): Unit =
    underlying.info(marker, withSource(message))

  def info(marker: Marker, message: String, cause: Throwable)(implicit source: Source): Unit =
    underlying.info(marker, withSource(message), cause)

  def info(marker: Marker, message: String, args: Any*)(implicit source: Source): Unit =
    underlying.info(marker, withSource(message), args)

  // Debug

  def debug(message: String)(implicit source: Source): Unit =
    underlying.debug(withSource(message))

  def debug(message: String, cause: Throwable)(implicit source: Source): Unit =
    underlying.debug(withSource(message), cause)

  def debug(message: String, builder: LogBuilder)(implicit source: Source): Unit =
    underlying.debug(withSource(message), builder)

  def debug(message: String, args: Any*)(implicit source: Source): Unit =
    underlying.debug(withSource(message), args)

  def debug(marker: Marker, message: String)(implicit source: Source): Unit =
    underlying.debug(withSource(message), message)

  def debug(marker: Marker, message: String, cause: Throwable)(implicit source: Source): Unit =
    underlying.debug(withSource(message), message, cause)

  def debug(marker: Marker, message: String, args: Any*)(implicit source: Source): Unit =
    underlying.debug(withSource(message), message, args)

  // Trace

  def trace(message: String)(implicit source: Source): Unit =
    underlying.trace(withSource(message))

  def trace(message: String, cause: Throwable)(implicit source: Source): Unit =
    underlying.trace(withSource(message), cause)

  def trace(message: String, builder: LogBuilder)(implicit source: Source): Unit =
    underlying.trace(withSource(message), builder)

  def trace(message: String, args: Any*)(implicit source: Source): Unit =
    underlying.trace(withSource(message), args)

  def trace(marker: Marker, message: String)(implicit source: Source): Unit =
    underlying.trace(marker, withSource(message))

  def trace(marker: Marker, message: String, cause: Throwable)(implicit source: Source): Unit =
    underlying.trace(marker, withSource(message), cause)

  def trace(marker: Marker, message: String, args: Any*)(implicit source: Source): Unit =
    underlying.trace(marker, withSource(message), args)

  private[logless] def withSource(other: String)(implicit source: Source): String =
    if (showSource) {
      source.enclosingMethod match {
        case Some(method) => s"${source.enclosingClass}.$method(...) - $other"
        case None         => s"${source.enclosingClass} - $other"
      }
    } else {
      other
    }

}