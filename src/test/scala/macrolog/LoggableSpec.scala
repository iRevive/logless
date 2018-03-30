package macrolog

import org.scalatest.{MustMatchers, WordSpecLike}

/**
  * @author Maksim Ochenashko
  */
class LoggableSpec extends WordSpecLike with MustMatchers {

  import Loggable.ops._

  "Loggable" should {

    "correctly use predefined instances" in {

      (new RuntimeException("123"): Throwable).print mustBe "RuntimeException(123)"
      (new RuntimeException(): Throwable).print mustBe "RuntimeException(<empty message>)"

      Map("123" -> 321, "test" -> 531).print mustBe "[(123, 321), (test, 531)]"
      List(1, 2, 3).print mustBe "[1, 2, 3]"
      Seq(1, 2, 3).print mustBe "[1, 2, 3]"

      Option(1).print mustBe "Some(1)"
      Option.empty[String].print mustBe "None"

      (1, "2").print mustBe "(1, 2)"
      (1L, 2, "3").print mustBe "(1, 2, 3)"
      (0f, 3, "4", List("321")).print mustBe "(0.0, 3, 4, [321])"

      (Right(123): Either[String, Int]).print mustBe "Right(123)"
      (Left("321"): Either[String, Int]).print mustBe "Left(321)"

      val pos = Position.generate

      pos.print mustBe s"Pos(${pos.fullPosition})"

    }

  }

}
