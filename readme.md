## Yet another logging tool.

#### Usage:

```scala
import com.logless._
import com.logless.auto._

@loggable // <- auto generated Loggable type class
case class User(firstName: String, lastName: String, @exclude password: String)

@loggable
case class Account(login: String, @exclude password: String) {

  @include val field: String = "something"

  @include def getterLikeDef(): Int = 123

}

case class Entity(id: Int, user: User)

object Entity {

  implicit val loggable: Loggable[Entity] = new Loggable[Entity] {
    override def present(value: Entity): String = 
      s"Entity(${value.id}, ${Loggable[User].present(value.user)})"
  }

}

class Service extends LazyLogging {

  def persist(entity: Entity): Unit = {
    sourceLogger.info("To persist: {}", ~entity)
  }

  def foo(account: Account, users: List[User]): Unit = {
    val value: Long = 1234576
    logger.info("Values: {}", value :+: account :+: users)
  }

}

object Main {

  def main(args: Array[String]): Unit = {
    val service = new Service()

    val user = User("Paolo", "Columbus", "qwerty")
    val entity = Entity(1, user)

    service.persist(entity)
    //logger will print: 'Service.persist(...) - To persist: Entity(1, User(Paolo, Columbus))'

    val account = Account("login", "password")
    service.foo(account, user :: Nil)
    //logger will print: 'Values: 1234576, Account(login, something, 123), [User(Paolo, Columbus)]'
  }

}
```