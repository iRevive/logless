package com.logless.builder

import com.logless.builder

/**
  * @author Maksim Ochenashko
  */
final class LogBuilderOps[+X <: LogBuilder](value: X) {

  def :+:[R: Loggable](that: R): R :+: X = builder.:+:(that, value)

  def unary_~ : LogBuilder = value

}
