package com.logless

/**
  * @author Maksim Ochenashko
  */
final class LogBuilderOps[+X <: LogBuilder](value: X) {

  def :+:[R: Loggable](that: R): R :+: X = com.logless.:+:(that, value)

  def unary_~ : LogBuilder = value

}
