package macrolog.auto

import org.scalatest.{MustMatchers, WordSpecLike}

import scala.meta._
import scala.meta.internal.inline.AbortException
import scala.meta.testkit._

/**
  * @author Maksim Ochenashko
  */
class LoggableTest extends WordSpecLike with MustMatchers {

  "@loggable" should {

    "generate a companion object for a trait" in {

      val obtained = LoggableMacroImpl.expandTrait(
        q"""
            trait Test {

              @loggable.include val property1: Int = 123

              @loggable.include def property2: String

              def property3: Long

            }
       """
      )

      val expected =
        q"""
        trait Test {

          @loggable.include val property1: Int = 123

          @loggable.include def property2: String

          def property3: Long
        }

        object Test {
          import _root_.macrolog.{LogSchema, Loggable}
          import _root_.macrolog.Loggable._

          implicit val logSchemaInstance: LogSchema[Test] = new LogSchema[Test] {
            override def schema(value: Test): Map[String, String] = {
              Map(("property1", Loggable[Int].print(value.property1)), ("property2", Loggable[String].print(value.property2)))
            }
          }

          implicit val loggableInstance: Loggable[Test] = Loggable.instance { value =>
            val body = logSchemaInstance.schema(value)
              .map({case (prop, v) => prop + " = " + v})
              .mkString("(", ", ", ")")

            "Test" + body
          }
        }
       """

      assertStructurallyEqual(obtained, expected)
    }

    "expand existing companion object of a trait" in {

      val obtained = LoggableMacroImpl.expandTrait(
        q"""
            trait Test {

              @loggable.include val property1: Int = 123

              @loggable.include def property2: String

              def property3: Long

            }
         """,
        q"""
            object Test
        """
      )

      val expected =
        q"""
        trait Test {

          @loggable.include val property1: Int = 123

          @loggable.include def property2: String

          def property3: Long
        }

        object Test {

          import _root_.macrolog.{LogSchema, Loggable}
          import _root_.macrolog.Loggable._

          implicit val logSchemaInstance: LogSchema[Test] = new LogSchema[Test] {
            override def schema(value: Test): Map[String, String] = {
              Map(("property1", Loggable[Int].print(value.property1)), ("property2", Loggable[String].print(value.property2)))
            }
          }

          implicit val loggableInstance: Loggable[Test] = Loggable.instance { value =>
            val body = logSchemaInstance.schema(value)
              .map({case (prop, v) => prop + " = " + v})
              .mkString("(", ", ", ")")

            "Test" + body
          }

        }
       """

      assertStructurallyEqual(obtained, expected)
    }

    "expand body of existing companion object of a trait" in {

      val obtained = LoggableMacroImpl.expandTrait(
        q"""
            trait Test {

              @loggable.include val property1: Int = 123

              @loggable.include def property2: String

              def property3: Long

            }
         """,
        q"""
            object Test {

              def companionMethod(argument: Int): String = "any"

            }
        """
      )

      val expected =
        q"""
        trait Test {

          @loggable.include val property1: Int = 123

          @loggable.include def property2: String

          def property3: Long
        }

        object Test {

          def companionMethod(argument: Int): String = "any"

          import _root_.macrolog.{LogSchema, Loggable}
          import _root_.macrolog.Loggable._

          implicit val logSchemaInstance: LogSchema[Test] = new LogSchema[Test] {
            override def schema(value: Test): Map[String, String] = {
              Map(("property1", Loggable[Int].print(value.property1)), ("property2", Loggable[String].print(value.property2)))
            }
          }

          implicit val loggableInstance: Loggable[Test] = Loggable.instance { value =>
            val body = logSchemaInstance.schema(value)
              .map({case (prop, v) => prop + " = " + v})
              .mkString("(", ", ", ")")

            "Test" + body
          }

        }
       """

      assertStructurallyEqual(obtained, expected)
    }

    "generate a companion object for a class" in {

      val obtained = LoggableMacroImpl.expandClass(
        q"""
            class Test(property1: Int, var property2: String, @loggable.exclude property3: Long) {

              @loggable.include def method1: String

              @loggable.include def method2(): String

              def method3: String

            }
       """
      )

      val expected =
        q"""
        class Test(property1: Int, var property2: String, @loggable.exclude property3: Long) {

          @loggable.include def method1: String

          @loggable.include def method2(): String

          def method3: String

        }

        object Test {
          import _root_.macrolog.{ LogSchema, Loggable }
          import _root_.macrolog.Loggable._

          implicit val logSchemaInstance: LogSchema[Test] = new LogSchema[Test] {
            override def schema(value: Test): Map[String, String] = {
              Map(
                ("property2", Loggable[String].print(value.property2)),
                ("method1", Loggable[String].print(value.method1)),
                ("method2", Loggable[String].print(value.method2))
              )
            }
          }

          implicit val loggableInstance: Loggable[Test] = Loggable.instance { value =>
            val body = logSchemaInstance.schema(value)
            .map({case (prop, v) => prop + " = " + v})
            .mkString("(", ", ", ")")

            "Test" + body
          }
        }

       """

      assertStructurallyEqual(obtained, expected)
    }

    "expand an existing companion object of a class" in {

      val obtained = LoggableMacroImpl.expandClass(
        q"""
            class Test(property1: Int, val property2: String, @loggable.exclude property3: Long) {

              @loggable.include def method1: String

              @loggable.include def method2(): String

              def method3: String

            }
         """,
        q"""
            object Test {

              def companionMethod(argument: Int): String = "any"

            }
        """
      )

      val expected =
        q"""
        class Test(property1: Int, val property2: String, @loggable.exclude property3: Long) {

          @loggable.include def method1: String

          @loggable.include def method2(): String

          def method3: String

        }

        object Test {

          def companionMethod(argument: Int): String = "any"

          import _root_.macrolog.{ LogSchema, Loggable }
          import _root_.macrolog.Loggable._

          implicit val logSchemaInstance: LogSchema[Test] = new LogSchema[Test] {
            override def schema(value: Test): Map[String, String] = {
              Map(
                ("property2", Loggable[String].print(value.property2)),
                ("method1", Loggable[String].print(value.method1)),
                ("method2", Loggable[String].print(value.method2))
              )
            }
          }

          implicit val loggableInstance: Loggable[Test] = Loggable.instance { value =>
            val body = logSchemaInstance.schema(value)
            .map({case (prop, v) => prop + " = " + v})
            .mkString("(", ", ", ")")

            "Test" + body
          }

        }
       """

      assertStructurallyEqual(obtained, expected)
    }

    "expand an existing companion object of a case class" in {

      val obtained = LoggableMacroImpl.expandClass(
        q"""
            case class Test(property1: Int, property2: String, @loggable.exclude property3: Long) {

              @loggable.include def method1: String

              @loggable.include def method2(): String

              def method3: String

            }
         """,
        q"""
            object Test {

              def companionMethod(argument: Int): String = "any"

            }
        """
      )

      val expected =
        q"""
        case class Test(property1: Int, property2: String, @loggable.exclude property3: Long) {

          @loggable.include def method1: String

          @loggable.include def method2(): String

          def method3: String

        }

        object Test {

          def companionMethod(argument: Int): String = "any"

          import _root_.macrolog.{ LogSchema, Loggable }
          import _root_.macrolog.Loggable._

          implicit val logSchemaInstance: LogSchema[Test] = new LogSchema[Test] {
            override def schema(value: Test): Map[String, String] = {
              Map(
                ("property1", Loggable[Int].print(value.property1)),
                ("property2", Loggable[String].print(value.property2)),
                ("method1", Loggable[String].print(value.method1)),
                ("method2", Loggable[String].print(value.method2))
              )
            }
          }

          implicit val loggableInstance: Loggable[Test] = Loggable.instance { value =>
            val body = logSchemaInstance.schema(value)
            .map({case (prop, v) => prop + " = " + v})
            .mkString("(", ", ", ")")

            "Test" + body
          }

        }
       """

      assertStructurallyEqual(obtained, expected)
    }

    "fail in case of `private[this]` modifier on def" in {

      val exception = intercept[AbortException] {
        LoggableMacroImpl.expandClass(
          q"""
            class Test(property1: Int, val property2: String, @loggable.exclude property3: Long) {

              @loggable.include private[this] def method1: String

              @loggable.include def method2(): String

              def method3: String

            }
       """
        )
      }

      val expectedMessage = "Member [method1] has one of restricted modifiers: `private[this]` or `protected[this]`"

      exception.message mustBe expectedMessage
    }

    "fail in case of missing explicit result type on def" in {

      val exception = intercept[AbortException] {
        LoggableMacroImpl.expandClass(
          q"""
            class Test(property1: Int, val property2: String, @loggable.exclude property3: Long) {

              @loggable.include def method1 = "321"

              @loggable.include var variable: String

              def method3: String

            }
       """
        )
      }

      val expectedMessage = "Member [method1] type must be declared explicitly"

      exception.message mustBe expectedMessage
    }

    "fail in case of a generic def" in {

      val exception = intercept[AbortException] {
        LoggableMacroImpl.expandClass(
          q"""
            class Test(property1: Int, val property2: String, @loggable.exclude property3: Long) {

              @loggable.include def method1(arg: Int): String

              @loggable.include def method2(): String

              def method3: String

            }
       """
        )
      }

      val expectedMessage = "Member [method1] must be declared without arguments"

      exception.message mustBe expectedMessage
    }

    "fail in case of `private[this]` modifier on val" in {

      val exception = intercept[AbortException] {
        LoggableMacroImpl.expandClass(
          q"""
            class Test(property1: Int, val property2: String, @loggable.exclude property3: Long) {

              @loggable.include private[this] val bodyProperty1: String

              @loggable.include def method2(): String

              def method3: String

            }
       """
        )
      }

      val expectedMessage = "Member [bodyProperty1] has one of restricted modifiers: `private[this]` or `protected[this]`"

      exception.message mustBe expectedMessage
    }

  }

  private def assertStructurallyEqual(obtained: Tree, expected: Tree): Unit = {
    StructurallyEqual(obtained, expected) match {
      case Left(AnyDiff(x, y)) =>
        fail(
          s"""Not Structurally equal!:
             |obtained: $x
             |expected: $y
             """.stripMargin)

      case _ =>
    }
  }

}
