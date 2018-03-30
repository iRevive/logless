package macrolog

import ch.qos.logback.classic.spi.LoggingEvent
import macrolog.TraceQualifier.DefinedTrace
import org.scalatest.{MustMatchers, WordSpec}

/**
  * @author Maksim Ochenashko
  */
class ConvertersSpec extends WordSpec with MustMatchers {

  import Implicits._

  "TraceQualifierConverter" should {

    "render a trace id" in {
      val converter = new TraceQualifierConverter

      val traceId: DefinedTrace = TraceId()
      val pos = Position.generate

      val ctx = PositionLoggingContext(traceId, pos)

      val event = new LoggingEvent
      event.setArgumentArray(Array(ctx))

      converter.convert(event) mustBe traceId.asString
    }

    "return 'undefined' in case of a missing context" in {
      val converter = new TraceQualifierConverter

      val event = new LoggingEvent

      converter.convert(event) mustBe "undefined"
    }

  }

  "PositionConverter" should {

    "render a position" in {
      val converter = new PositionConverter

      val traceId: DefinedTrace = TraceId()
      val pos = Position.generate

      val ctx = PositionLoggingContext(traceId, pos)

      val event = new LoggingEvent
      event.setArgumentArray(Array(ctx))

      converter.convert(event) mustBe pos.fullPosition
    }

    "return 'undefined' in case of a missing position" in {
      val converter = new PositionConverter

      val event = new LoggingEvent

      converter.convert(event) mustBe "undefined"
    }

  }

}