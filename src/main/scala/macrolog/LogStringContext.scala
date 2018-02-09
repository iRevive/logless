package macrolog

import macrolog.auto.LogStringContextMacro

import scala.language.experimental.macros
import scala.language.implicitConversions

/**
  * @author Maksim Ochenashko
  */
final class LogStringContext(val sc: StringContext) extends AnyVal {

  def log(args: Any*): String = macro LogStringContextMacro.logImpl

}

trait LogStringContextConversion {

  @inline
  implicit def sc2logCtx(sc: StringContext): LogStringContext = new LogStringContext(sc)

}

object LogStringContextConversion extends LogStringContextConversion