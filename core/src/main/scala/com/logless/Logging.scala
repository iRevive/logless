package com.logless

import org.slf4j.LoggerFactory

/**
  * @author Maksim Ochenashko
  */
trait LazyLogging {

  protected lazy val sourceLogger: SourceLogger =
    SourceLogger(LoggerFactory.getLogger(getClass.getName))

  protected lazy val traceLogger: TraceLogger =
    TraceLogger(LoggerFactory.getLogger(getClass.getName))

}

trait StrictLogging {

  protected val sourceLogger: SourceLogger =
    SourceLogger(LoggerFactory.getLogger(getClass.getName))

  protected val traceLogger: TraceLogger =
    TraceLogger(LoggerFactory.getLogger(getClass.getName))

}