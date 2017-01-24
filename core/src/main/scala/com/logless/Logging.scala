package com.logless

import org.slf4j.LoggerFactory

/**
  * @author Maksim Ochenashko
  */
trait LazyLogging {

  protected lazy val sourceLogger: SourceLogger =
    SourceLogger(LoggerFactory.getLogger(getClass.getName), showSource = true)

  protected lazy val logger: SourceLogger =
    SourceLogger(LoggerFactory.getLogger(getClass.getName), showSource = false)

}

trait StrictLogging {

  protected val sourceLogger: SourceLogger =
    SourceLogger(LoggerFactory.getLogger(getClass.getName), showSource = true)

  protected val logger: SourceLogger =
    SourceLogger(LoggerFactory.getLogger(getClass.getName), showSource = false)

}