package macrolog

import macrolog.auto.Pos
import org.scalatest.{MustMatchers, WordSpecLike}

import scala.util.Try
import scala.util.control.NonFatal

/**
 * @author Maksim Ochenashko
 */
class PosSpec extends WordSpecLike with MustMatchers {

  "Pos" must {

    "correctly extract enclosing class & method" in {

      fakeMethod() mustBe Pos("PosSpec", Some("fakeMethod"), "macrolog.PosSpec.fakeMethod:39")

      lambda() mustBe Pos("PosSpec", Some("lambda"), "macrolog.PosSpec.lambda:44")

      partialFunction().apply(()) mustBe Pos("PosSpec", Some("partialFunction"), "macrolog.PosSpec.partialFunction:49")

      recoverLambda() mustBe Pos("PosSpec", Some("recoverLambda"), "macrolog.PosSpec.recoverLambda:54")

      val fakeClass = new FakeClass

      fakeClass.method() mustBe Pos("FakeClass", Some("method"), "macrolog.PosSpec.FakeClass.method:59")

      fakeClass.lambda() mustBe Pos("FakeClass", Some("lambda"), "macrolog.PosSpec.FakeClass.lambda:62")

      fakeClass.forComprehension() mustBe Pos("FakeClass", Some("forComprehension"), "macrolog.PosSpec.FakeClass.forComprehension:67")

      fakeClass.generatedPos() mustBe Pos("FakeClass", Some("generatedPos"), "macrolog.PosSpec.FakeClass.generatedPos:70")

    }
  }

  def fakeMethod(): Pos = {
    Pos.generate
  }

  def lambda(): Pos = {
    Some("x").map { _ =>
      Pos.generate
    }.get
  }

  def partialFunction(): PartialFunction[Unit, Pos] = {
    case _ => Pos.generate
  }

  def recoverLambda(): Pos = {
    Try(throw new RuntimeException("")).recover { case NonFatal(_) => Pos.generate }.get
  }

  private class FakeClass {

    def method(): Pos = Pos.generate

    def lambda(): Pos = {
      Some("x").map(_ => Pos.generate).get
    }

    def forComprehension(): Pos =
      (for {
        _ <- Some("x")
      } yield Pos.generate).get

    def generatedPos(): Pos = {
      outerImplicitPos()
    }

    def outerImplicitPos()(implicit pos: Pos): Pos = pos
  }

}
