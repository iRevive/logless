package macrolog

import macrolog.auto.Position
import org.scalatest.{MustMatchers, WordSpecLike}

import scala.util.Try
import scala.util.control.NonFatal

/**
 * @author Maksim Ochenashko
 */
class PositionTest extends WordSpecLike with MustMatchers {

  "Position" must {

    "correctly extract enclosing class & method" in {

      fakeMethod() mustBe Position("PositionTest", Some("fakeMethod"), "macrolog.PositionTest.fakeMethod:39")

      lambda() mustBe Position("PositionTest", Some("lambda"), "macrolog.PositionTest.lambda:44")

      partialFunction().apply(()) mustBe Position("PositionTest", Some("partialFunction"), "macrolog.PositionTest.partialFunction:49")

      recoverLambda() mustBe Position("PositionTest", Some("recoverLambda"), "macrolog.PositionTest.recoverLambda:54")

      val fakeClass = new FakeClass

      fakeClass.method() mustBe Position("FakeClass", Some("method"), "macrolog.PositionTest.FakeClass.method:59")

      fakeClass.lambda() mustBe Position("FakeClass", Some("lambda"), "macrolog.PositionTest.FakeClass.lambda:62")

      fakeClass.forComprehension() mustBe Position("FakeClass", Some("forComprehension"), "macrolog.PositionTest.FakeClass.forComprehension:67")

      fakeClass.generatedPos() mustBe Position("FakeClass", Some("generatedPos"), "macrolog.PositionTest.FakeClass.generatedPos:70")

    }
  }

  def fakeMethod(): Position = {
    Position.generate
  }

  def lambda(): Position = {
    Some("x").map { _ =>
      Position.generate
    }.get
  }

  def partialFunction(): PartialFunction[Unit, Position] = {
    case _ => Position.generate
  }

  def recoverLambda(): Position = {
    Try(throw new RuntimeException("")).recover { case NonFatal(_) => Position.generate }.get
  }

  private class FakeClass {

    def method(): Position = Position.generate

    def lambda(): Position = {
      Some("x").map(_ => Position.generate).get
    }

    def forComprehension(): Position =
      (for {
        _ <- Some("x")
      } yield Position.generate).get

    def generatedPos(): Position = {
      outerImplicitPos()
    }

    def outerImplicitPos()(implicit pos: Position): Position = pos
  }

}