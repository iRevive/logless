package macrolog

import macrolog.auto.loggable
import org.scalatest.{MustMatchers, WordSpecLike}

/**
 * @author Maksim Ochenashko
 */
class LogSchemaTest extends WordSpecLike with MustMatchers {

  "LogSchema" should {

    "map object to map" in {
      case class CustomObject(propertyA: String, propertyB: Int)

      val instance = CustomObject("myPropertyA", 1001)

      val logSchema =
        new LogSchema[CustomObject] {
          override def schema(value: CustomObject): Map[String, String] =
            Map(
              "propertyA" -> value.propertyA,
              "propertyB" -> value.propertyB.toString
            )
        }

      logSchema.schema(instance) mustBe Map("propertyA" -> "myPropertyA", "propertyB" -> "1001")
    }

    "generate LogSchema and Loggable instance via @logging.schema macros" in {
      @loggable
      case class CustomObject(propertyA: String, propertyB: Int, @loggable.exclude propertyC: String)

      val instance = CustomObject("myPropertyA", 1001, "prop3")

      LogSchema[CustomObject].schema(instance) mustBe Map("propertyA" -> "myPropertyA", "propertyB" -> "1001")
      Loggable[CustomObject].print(instance) mustBe "CustomObject(propertyA = myPropertyA, propertyB = 1001)"
    }

    "generate Loggable type class with included fields" in {
      @loggable
      case class Test(a: Int, b: String) {

        @loggable.exclude val prop: Int = 123

        @loggable.include def name: String = "name value"

        @loggable.include def nestedProp(): Double = 14.01

      }

      val instance = Test(1, "value")

      val expectedMap = Map("a" -> "1", "b" -> "value", "name" -> "name value", "nestedProp" -> "14.01")

      LogSchema[Test].schema(instance) mustBe expectedMap

      val body = expectedMap.map { case (prop, v) => s"$prop = $v" }.mkString(", ")

      Loggable[Test].print(instance) mustBe s"Test($body)"
    }

    "generate Loggable type class" in {

      @loggable
      case class Test(a: Int, b: String, c: Int)

      val test = Test(1, "value", 3)

      Test.loggableInstance.print(test) mustBe "Test(a = 1, b = value, c = 3)"

      @loggable
      case class Test2(a: Int, b: String, test: Test)

      val test2 = Test2(1, "value_2", test)

      Loggable[Test2].print(test2) mustBe "Test2(a = 1, b = value_2, test = Test(a = 1, b = value, c = 3))"

      @loggable
      case class Test3(a: Int, b: String, c: List[Test])

      val test3 = Test3(1, "value_3", test :: test.copy(c = 4) :: Nil)

      Loggable[Test3].print(test3) mustBe "Test3(a = 1, b = value_3, c = [Test(a = 1, b = value, c = 3), Test(a = 1, b = value, c = 4)])"
    }

    "generate Loggable type class without excluded fields" in {

      @loggable
      case class Test(a: Int, b: String, @loggable.exclude c: Int)

      Loggable[Test].print(Test(1, "value", 5)) mustBe "Test(a = 1, b = value)"
    }

    "generate Loggable type class for trait and abstract class" in {

      @loggable
      trait SimpleTrait {

        @loggable.include val prop: Int

        @loggable.include val name: String

        @loggable.include def defProp: String

      }

      class SimpleClass extends SimpleTrait {
        val prop = 321

        val name = "test name"

        def defProp = "test"
      }

      Loggable[SimpleTrait].print(new SimpleClass) mustBe "SimpleTrait(prop = 321, name = test name, defProp = test)"

      @loggable
      sealed abstract class AbstractClass(val value: Int)

      class Clazz extends AbstractClass(1)

      Loggable[AbstractClass].print(new Clazz) mustBe "AbstractClass(value = 1)"
    }

  }

}