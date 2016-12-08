package com.logless

import org.slf4j.LoggerFactory

/**
  * @author Maksim Ochenashko
  */
trait LazyLogging {

  protected lazy val sourceLogger: Logger =
    Logger(LoggerFactory.getLogger(getClass.getName), showSource = true)

  protected lazy val logger: Logger =
    Logger(LoggerFactory.getLogger(getClass.getName), showSource = false)

}

trait StrictLogging {

  protected val sourceLogger: Logger =
    Logger(LoggerFactory.getLogger(getClass.getName), showSource = true)

  protected val logger: Logger =
    Logger(LoggerFactory.getLogger(getClass.getName), showSource = false)

}