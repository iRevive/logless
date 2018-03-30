package macrolog

import macrolog.LogStringContextConversion._
import macrolog.auto.loggable
import org.scalatest.{MustMatchers, WordSpecLike}

/**
  * @author Maksim Ochenashko
  */
class LogStringContextSpec extends WordSpecLike with MustMatchers {

  "LogStringContext" must {

    "correctly build string using Loggable typeclass" in {

      @loggable
      case class A(value: String, @loggable.exclude value2: String)

      val arg1 = A("my value", "exclude value")
      val arg2 = "something interesting"
      val arg3 = 123
      def arg4() = 124L

      val result = log"String interpolation and macro: arg1: [$arg1, $arg2, $arg3, ${arg4()}]"

      val expected = "String interpolation and macro: arg1: [A(value = my value), something interesting, 123, 124]"

      result mustBe expected
    }

    "return string as is for empty arguments" in {
      val result = log"String without interpoliation"
      val expected = "String without interpoliation"

      result mustBe expected
    }

  }

}
