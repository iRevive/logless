# Yet another logging tool
![Bintray](https://img.shields.io/bintray/v/irevive/maven/logless.svg)

## Installation:

```
resolvers ++= Resolver.bintrayRepo("irevive", "maven")

libraryDependencies += "io.github.irevive" %% "logless" % version
```

## Loggers
Library provides 2 loggers: SourceLogger and TraceLogger.
By default loggers defined at the `LazyLogging` and `StrictLogging` traits.

##### TraceLogger
TraceLogger prepends log message with the trace id. Trace ID can be passed implicitly.

Example:
```scala
class Service extends LazyLogging {

  def find(name: String)(implicit traceId: TraceID): Unit = {
    sourceLogger.debug("Searching entity with name [{}]", name)
    //prints: '[820d8ce6-4465-4d49-aa19-192aa557b428]: Service.find(...) - Searching entity with name [name]'
  }

}

```

##### SourceLogger
Source logger prints location (class name and method name) of the method call.  
SourceLogger supports irace id too.

Example:
## Usage:

```scala
case class Entity(id: Int, userName: String)
  
class Service extends LazyLogging {
 
  def persist(entity: Entity): Unit = {
    sourceLogger.debug("Entity [{}] persisted", entity.id)
    //prints: 'Service.persist(...) - Entity [id] persisted'
  }
  
  def load(id: String)(implicit traceId: TraceID): Unit = {
    sourceLogger.info("Loading entity [{}]", id)
    //prints: '[820d8ce6-4465-4d49-aa19-192aa557b428]: Service.load(...) - Loading entity [id]'
  }
 
}
```

## LogBuilder
LogBuilder allows to build a log message from multiple elements.
LogBuilder requires an implicit `Loggable[A]` instance for each element.
Elements are separated by a comma.

Usage:
```scala
import com.logless.auto._
import com.logless.builder._
 
@loggable
case class User(firstName: String, lastName: String)
 
@loggable
case class Device(id: String, name: String)
 
val user = User("Paolo", "Columbus")
val device = Device("identifier", "device name")
 
TraceLogger(this.getClass).info("My log message: {}", user :+: device)
//prints: 'My log message: User(Paolo, Columbus), Device(identifier, device name)'
```

## Macro generation
Loggable instance can be generated via macro-annotation `@loggable` .  
`@exclude` used to ignore case class field.  
`@include` used to add non case class primary fields. `val`, `var` and `def` without arguments are supported.

Example:
```scala
@loggable
case class User(firstName: String, lastName: String, @exclude password: String)
 
@loggable
case class Account(login: String, @exclude password: String, user: User) {

  @include val field: String = "something"

  @include def getterLikeDef(): Int = 123

}
```

Generated code:
```scala
case class User(firstName: String, lastName: String, password: String)
 
object User {

  implicit val loggable: Loggable[User] = new Loggable[User] {
    override def present(value: User): String =
      s"User(${value.firstName}, ${value.lastName})"
  }

}
 
case class Account(login: String, password: String, user: User) {

  val field: String = "something"

  def getterLikeDef(): Int = 123

}
  
object Account {

  implicit val loggable: Loggable[Account] = new Loggable[Account] {
    override def present(value: Account): String = 
      s"Account(${value.id}, ${Loggable[User].present(value.user)}, ${value.field}, ${value.getterLikeDef()})"
  }

}
```

Usage:
```scala
import com.logless.auto._
import com.logless.builder._
 
val user = User("Paolo", "Columbus", "qwerty")
val entity = Account("login", "password", user)
    
TraceLogger(this.getClass).info("Entity: {}", ~entity)
//prints: 'Entity: Account(login, User(Paolo, Columbus), something, 123)'
```

`~` used to convert entity to `LogBuilder` type.

