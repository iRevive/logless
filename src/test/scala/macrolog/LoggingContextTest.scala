package macrolog

import macrolog.PositionLoggingContext.PositionLoggingContextImpl
import macrolog.TraceQualifier.TraceId
import macrolog.TraceQualifierLoggingContext.TraceQualifierLoggingContextImpl
import macrolog.auto.Position
import org.scalamock.matchers.MockParameter
import org.scalamock.scalatest.MockFactory
import org.scalatest.{MustMatchers, WordSpecLike}

/**
  * @author Maksim Ochenashko
  */
class LoggingContextTest extends WordSpecLike with MustMatchers with MockFactory {

  import macrolog.Implicits._

  "LoggingContext" should {

    "generate context from an implicit traceId" in {
      implicit val traceId: TraceId = TraceId()
      implicit val pos: Position = Position.generate

      val service = mock[Service]

      val matcher = contextMatcher[TraceQualifierLoggingContextImpl] { ctx =>
        ctx.traceQualifier == traceId && ctx.position == pos
      }

      (service.withCtx()(_: LoggingContext)).expects(matcher).once()

      service.withCtx()
    }

    "generate context from a position" in {
      implicit val pos: Position = Position.generate

      val service = mock[Service]

      val matcher = contextMatcher[PositionLoggingContextImpl] { ctx =>
        ctx.position == pos
      }

      (service.withCtx()(_: LoggingContext)).expects(matcher).once()

      service.withCtx()
    }

    "generate context from a generated position" in {
      val service = mock[Service]

      val matcher = contextMatcher[PositionLoggingContextImpl] { ctx =>
        ctx.position == Position("LoggingContextTest", None, "macrolog.LoggingContextTest:14")
      }

      (service.withCtx()(_: LoggingContext)).expects(matcher).once()

      service.withCtx()
    }

    "update position for a custom context" in {
      val traceId: TraceId = TraceId()
      val pos: Position = Position.generate
      val value = "any random value"

      implicit val customContext: CustomContext = CustomContext(value, traceId, pos)

      val service = mock[Service]

      val matcher = contextMatcher[CustomContext] { ctx =>
        ctx.value == value && ctx.traceQualifier == traceId && ctx.position == pos
      }

      (service.withCtx()(_: LoggingContext)).expects(matcher).once()

      service.withCtx()
    }

  }

  private case class CustomContext(value: String, traceQualifier: TraceId, position: Position)
    extends TraceQualifierLoggingContext with PositionLoggingContext {

    override def withPosition(position: Position): PositionLoggingContext = copy(position = position)

  }

  private class Service {

    def withCtx()(implicit ctx: LoggingContext): Unit = ()

  }

  private def contextMatcher[A: Manifest](predicate: A => Boolean): MockParameter[LoggingContext] = {
    new MockParameter[LoggingContext](null: LoggingContext) {
      override def equals(that: Any): Boolean =
        that match {
          case c: A =>
            predicate(c)
          case _ =>
            false
        }
    }
  }
}
