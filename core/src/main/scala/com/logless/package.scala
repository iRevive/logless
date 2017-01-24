package com

import com.logless.builder._

import scala.language.implicitConversions

/**
  * @author Maksim Ochenashko
  */
package object logless {

  implicit def logBuilderOps[X <: LogBuilder](builder: X): LogBuilderOps[X] = new LogBuilderOps[X](builder)

  implicit def toBuilderOps[X: Loggable](value: X): LogBuilderOps[X :+: End] =
    new LogBuilderOps(LogBuilder(value))

}
