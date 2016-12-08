package com.logless

import org.scalatest.{MustMatchers, WordSpecLike}
import com.typesafe.scalalogging.{Logger => ScalaLogger}

/**
  * @author Maksim Ochenashko
  */
class LoggerTest extends WordSpecLike with MustMatchers {

  "Logger" must {

    "add enclosing class & method name to logging message if 'showSource' enabled" in {
      val sourceLogger = Logger(ScalaLogger("test logger"), showSource = true)

      val message = "Simple logging message"

      fakeMethod(sourceLogger, message) mustBe s"LoggerTest.fakeMethod0(...) - $message"

      new FakeClass().method(sourceLogger, message) mustBe s"FakeClass.method(...) - $message"

      lambda(sourceLogger, message) mustBe s"LoggerTest.lambda(...) - $message"

      new FakeClass().lambda(sourceLogger, message) mustBe s"FakeClass.lambda(...) - $message"

      new FakeClass().forComprehension(sourceLogger, message) mustBe s"FakeClass.forComprehension(...) - $message"
    }

    "return message as is if 'showSource' disabled" in {
      val logger = Logger(ScalaLogger("test logger"), showSource = false)

      val message = "Simple logging message"

      fakeMethod(logger, message) mustBe message

      new FakeClass().method(logger, message) mustBe message

      lambda(logger, message) mustBe message

      new FakeClass().lambda(logger, message) mustBe message

      new FakeClass().forComprehension(logger, message) mustBe message
    }
  }

  def fakeMethod(logger: Logger, message: String): String = {
    def fakeMethod0(message: String): String =
      logger withSource message

    fakeMethod0(message)
  }

  def lambda(logger: Logger, message: String): String = {
    Some("x").map(r => logger.withSource(message)).get
  }

  class FakeClass {

    def method(logger: Logger, message: String): String =
      logger withSource message

    def lambda(logger: Logger, message: String): String = {
      Some("x").map(r => logger.withSource(message)).get
    }

    def forComprehension(logger: Logger, message: String): String =
      (for {
        _ <- Some("x")
      } yield logger.withSource(message)).get

  }

}
