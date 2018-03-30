package macrolog

import macrolog.TraceQualifierLoggingContext.TraceQualifierLoggingContextImpl
import org.scalamock.matchers.MockParameter
import org.scalamock.scalatest.MockFactory
import org.scalatest.{MustMatchers, WordSpecLike}

/**
  * @author Maksim Ochenashko
  */
class LoggingContextSpec extends WordSpecLike with MustMatchers with MockFactory {

  import macrolog.Implicits._

  "LoggingContext" should {

    "generate context from an implicit traceId" in {
      implicit val traceId: TraceId = TraceId()

      val service = mock[Service]

      val matcher = contextMatcher[TraceQualifierLoggingContextImpl] { ctx =>
        ctx.traceQualifier == traceId
      }

      (service.withCtx()(_: LoggingContext)).expects(matcher).once()

      service.withCtx()
    }

    "use a custom context" in {
      val traceId: TraceId = TraceId()
      val value = "any random value"

      implicit val customContext: CustomContext = CustomContext(value, traceId)

      val service = mock[Service]

      val matcher = contextMatcher[CustomContext] { ctx =>
        ctx.value == value && ctx.traceQualifier == traceId
      }

      (service.withCtx()(_: LoggingContext)).expects(matcher).once()

      service.withCtx()
    }

  }

  private case class CustomContext(value: String, traceQualifier: TraceId) extends TraceQualifierLoggingContext

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

      override def toString(): String = "MockedLoggingContext"
    }
  }
}
