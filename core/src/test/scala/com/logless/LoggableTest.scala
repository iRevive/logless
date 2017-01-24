package com.logless

import com.logless.auto.{exclude, include, loggable}
import com.logless.builder.Loggable
import org.scalatest.{MustMatchers, WordSpecLike}

/**
  * @author Maksim Ochenashko
  */
class LoggableTest extends WordSpecLike with MustMatchers  {

  @loggable
  case class Test4(a: Int, b: String, @exclude c: Int)

  "loggable" must {

    "generate Loggable type class" in {

      @loggable
      case class Test(a: Int, b: String, c: Int)

      val test = Test(1, "value", 3)

      Test.loggable.present(test) mustBe "Test(1, value, 3)"

      @loggable
      case class Test2(a: Int, b: String, test: Test)

      val test2 = Test2(1, "value_2", test)

      Loggable[Test2].present(test2) mustBe "Test2(1, value_2, Test(1, value, 3))"

      @loggable
      case class Test3(a: Int, b: String, c: List[Test])

      val test3 = Test3(1, "value_3", test :: test.copy(c = 4) :: Nil)

      Loggable[Test3].present(test3) mustBe "Test3(1, value_3, [Test(1, value, 3), Test(1, value, 4)])"
    }

    "generate Loggable type class without excluded fields" in {

      @loggable
      case class Test(a: Int, b: String, @exclude c: Int)

      Loggable[Test].present(Test(1, "value", 5)) mustBe "Test(1, value)"
    }

    "generate Loggable type class with included fields" in {

      @loggable
      case class Value(a: Int, b: String)

      @loggable
      case class Test(a: Int, b: String) {

        @include val prop: Int = 123

        @include def name: String = "name value"

        @include def nestedProp(): Value = Value(4, "%")

      }

      Loggable[Test].present(Test(1, "value")) mustBe "Test(1, value, 123, name value, Value(4, %))"
      assert(1 == 1)
    }

    "generate Loggable type class for trait and abstract class" in {

      @loggable
      trait SimpleTrait {

        @include val prop: Int

        @include val name: String

        @include def defProp: String

      }

      class SimpleClass extends SimpleTrait {
        val prop = 321

        val name = "test name"

        def defProp = "test"
      }

      Loggable[SimpleTrait].present(new SimpleClass) mustBe "SimpleTrait(321, test name, test)"

      @loggable
      sealed abstract class AbstractClass(val value: Int)

      class Clazz extends AbstractClass(1)

      Loggable[AbstractClass].present(new Clazz) mustBe "AbstractClass(1)"
    }

  }

}
