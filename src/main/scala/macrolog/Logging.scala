package macrolog

import org.slf4j.LoggerFactory

/**
 * @author Maksim Ochenashko
 */
sealed trait LoggingOps extends LogStringContextConversion

trait LazyLogging extends LoggingOps {

  protected lazy val logger: Logger =
    Logger(LoggerFactory.getLogger(getClass.getName))

}

trait StrictLogging extends LoggingOps {

  protected val logger: Logger =
    Logger(LoggerFactory.getLogger(getClass.getName))

}
