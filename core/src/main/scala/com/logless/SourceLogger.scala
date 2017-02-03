package com.logless

import com.logless.builder.LogBuilder
import com.logless.source.Source
import org.slf4j.{Marker, Logger => Underlying}
import com.typesafe.scalalogging.{Logger => ScalaLogger}

import scala.reflect.ClassTag

/**
  * @author Maksim Ochenashko
  */
object SourceLogger {

  def apply(scalaLogger: ScalaLogger): SourceLogger =
    new SourceLogger(TraceLogger(scalaLogger))

  def apply(traceLogger: TraceLogger): SourceLogger =
    new SourceLogger(traceLogger)

  def apply(underlying: Underlying): SourceLogger =
    new SourceLogger(TraceLogger(underlying))

  def apply(name: String): SourceLogger =
    new SourceLogger(TraceLogger(name))

  def apply(clazz: Class[_]): SourceLogger =
    new SourceLogger(TraceLogger(clazz))

  def apply[T](showSource: Boolean)(implicit ct: ClassTag[T]): SourceLogger =
    new SourceLogger(TraceLogger(ScalaLogger(ct)))

}

@SerialVersionUID(716196318)
final class SourceLogger private(val underlying: TraceLogger) extends Serializable {

  // Error

  def error(message: String)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.error(withSource(message))

  def error(message: String, cause: Throwable)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.error(withSource(message), cause)

  def error(message: String, builder: LogBuilder)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.error(withSource(message), builder)

  def error(message: String, args: Any*)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.error(withSource(message), args)

  def error(marker: Marker, message: String)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.error(marker, withSource(message))

  def error(marker: Marker, message: String, cause: Throwable)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.error(marker, withSource(message), cause)

  def error(marker: Marker, message: String, args: Any*)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.error(marker, withSource(message), args)

  // Warn

  def warn(message: String)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.warn(withSource(message))

  def warn(message: String, cause: Throwable)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.warn(withSource(message), cause)

  def warn(message: String, builder: LogBuilder)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.warn(withSource(message), builder)

  def warn(message: String, args: Any*)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.warn(withSource(message), args)

  def warn(marker: Marker, message: String)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.warn(marker, withSource(message))

  def warn(marker: Marker, message: String, cause: Throwable)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.warn(marker, withSource(message), cause)

  def warn(marker: Marker, message: String, args: Any*)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.warn(marker, withSource(message), args)

  // Info

  def info(message: String)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.info(withSource(message))

  def info(message: String, cause: Throwable)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.info(withSource(message), cause)

  def info(message: String, builder: LogBuilder)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.info(withSource(message), builder)

  def info(message: String, args: Any*)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.info(withSource(message), args)

  def info(marker: Marker, message: String)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.info(marker, withSource(message))

  def info(marker: Marker, message: String, cause: Throwable)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.info(marker, withSource(message), cause)

  def info(marker: Marker, message: String, args: Any*)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.info(marker, withSource(message), args)

  // Debug

  def debug(message: String)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.debug(withSource(message))

  def debug(message: String, cause: Throwable)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.debug(withSource(message), cause)

  def debug(message: String, builder: LogBuilder)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.debug(withSource(message), builder)

  def debug(message: String, args: Any*)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.debug(withSource(message), args)

  def debug(marker: Marker, message: String)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.debug(withSource(message), message)

  def debug(marker: Marker, message: String, cause: Throwable)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.debug(withSource(message), message, cause)

  def debug(marker: Marker, message: String, args: Any*)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.debug(withSource(message), message, args)

  // Trace

  def trace(message: String)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.trace(withSource(message))

  def trace(message: String, cause: Throwable)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.trace(withSource(message), cause)

  def trace(message: String, builder: LogBuilder)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.trace(withSource(message), builder)

  def trace(message: String, args: Any*)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.trace(withSource(message), args)

  def trace(marker: Marker, message: String)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.trace(marker, withSource(message))

  def trace(marker: Marker, message: String, cause: Throwable)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.trace(marker, withSource(message), cause)

  def trace(marker: Marker, message: String, args: Any*)(implicit source: Source, tracer: TraceIdentifier): Unit =
    underlying.trace(marker, withSource(message), args)

  private[logless] def withSource(other: String)(implicit source: Source, tracer: TraceIdentifier): String =
    source.enclosingMethod match {
      case Some(method) => s"${source.enclosingClass}.$method(...) - $other"
      case None         => s"${source.enclosingClass} - $other"
    }

}