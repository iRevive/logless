# Macrolog
[![Download](https://api.bintray.com/packages/irevive/maven/macrolog/images/download.svg)](https://bintray.com/irevive/maven/macrolog/_latestVersion)
[![Build Status](https://travis-ci.org/iRevive/macrolog.svg?branch=master)](https://travis-ci.org/iRevive/macrolog)
[![Coverage Status](https://coveralls.io/repos/github/iRevive/macrolog/badge.svg?branch=master)](https://coveralls.io/github/iRevive/macrolog?branch=master)

Macrolog provides a type-safe way to log entities.
 
## Summary
Main idea behind this library to provide a type-safe way to log entities.  
There are only two type classes:  
1) `macrolog.Loggable` - presents type A as string
2) `macrolog.LogSchema` - returns schema of the object A

Library built on top of [scala-logging](https://github.com/typesafehub/scala-logging)


## Installation:

```
resolvers += Resolver.bintrayRepo("irevive", "maven")

libraryDependencies += "io.github.irevive" %% "macrolog" % version

resolvers += Resolver.url("scalameta", url("http://dl.bintray.com/scalameta/maven"))(Resolver.ivyStylePatterns),

addCompilerPlugin("org.scalameta" %% "paradise" % "3.0.0-M10" cross CrossVersion.full)

scalacOptions += "-Xplugin-require:macroparadise"
```

## Usage
The logger defined at `LazyLogging` and `StrictLogging` traits.

Example:
```scala
class Service extends StrictLogging {

  def find(name: String)(implicit traceId: macrolog.TraceId): Unit = {
    logger.debug(s"Searching entity with name [$name]")
  }
  
  def find(name: String)(implicit ctx: macrolog.LoggingContext): Unit = {
    logger.debug(s"Searching entity with name [$name]")
  }

}
```

## Type-safe logging
`log` prefix will generate a type-safe string using `Loggable` instance for each argument of the string interpolation.  

Example:  
```scala
import macrolog.LogStringContextConversion._
  
@loggable
case class User(name: String, login: String, @loggable.exclude password: String)
  
val user: User = User("name", "login", "strong password")
 
val safeLogStatement = log"My typesafe log. User: [$user]"
```

This code will be transformed to:
```scala
case class User(name: String, login: String, @loggable.exclude password: String)  
  
val user: User = User("name", "login", "strong password")
 
val safeLogStatement = {
  import _root_.macrolog.Loggable
  StringContext("My typesafe log. User: [", "]").s(Loggable[User].print(user))
}
```

## How to add TraceQualifier and Position to the log message
Position and TraceId always calculated at the compile time. This library works without reflection
and has a zero overhead. 

#### Position set-up
It's not necessary to provide Position implicitly. A value automatically calculated at the compile time.   
If you want to log higher level of the call chain, just provide it implicitly to the method:  
```scala
def method(implicit pos: macrolog.auto.Pos): Unit = {
  logger.info("My log message")
}
```  

#### TraceId set-up
TraceId must be always specified as an implicit parameter of the method:  
```scala
def method1(implicit traceId: macrolog.TraceId): Unit = {
  logger.info("My log message 1")
}
  
def method2(implicit traceId: macrolog.TraceId): Unit = {
  logger.info("My log message 2")
}
  
def method3(implicit traceId: macrolog.TraceId): Unit = {
  logger.info("My log message 3")
}
```

Just generate a new traceId on the top of the call chain:
```scala
def entryPoint(): Unit = {
  implicit val traceId = macrolog.TraceId()
  
  method1()
  method2()
  method3()
}
```

#### Custom LoggingContext
```scala
case class CustomLoggingContext(user: String, timestamp: LocalDateTime, module: String, traceQualifier: macrolog.TraceId) 
  extends macrolog.TraceQualifierLoggingContext
```

#### Logback config
Add conversion rules `traceId` and `position` to the `logback.xml` configuration.
```xml
<conversionRule conversionWord="traceId" converterClass="macrolog.TraceQualifierConverter"/>
<conversionRule conversionWord="position" converterClass="macrolog.PositionConverter"/>
```
  
After that you can use them in the appender pattern:  
```
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
  <encoder>
    <pattern>%date %coloredLevel %thread %traceId %position - %message%n%xException</pattern>
  </encoder>
</appender>
```

The output will be:  
`2017-08-01 16:21:51,879 thread1 e12585zd-b81a-4e61-8933-7ccb530d07f2e com.example.Bootrstap.start:47 - Application started successfully`

  
## Macro generation
Loggable and LogSchema instances can be generated via macro-annotation `@loggable`.  
`@loggable.exclude` used to ignore case class field.  
`@loggable.include` used to add non case class primary fields. `val`, `var` and `def` without arguments are supported.  

**Important!** Always use `exclude` and `include` annotations with the prefix `loggable`.

Example:
```scala
@loggable
case class User(firstName: String, lastName: String, @loggable.exclude password: String)
 
@loggable
case class Account(login: String, @loggable.exclude password: String, user: User) {
 
  @loggable.include val field: String = "something"
  
  @loggable.include def getterLikeDef(): Int = 123
  
}
```

Generated code:
```scala
case class User(firstName: String, lastName: String, password: String)
 
object User {
  import _root_.macrolog.{LogSchema, Loggable}
  import _root_.macrolog.Loggable._
  
  implicit val logSchemaInstance: LogSchema[User] = new LogSchema[User] {
    override def schema(value: User): List[(String, String)] = {
      List(("firstName", Loggable[String].print(value.firstName)), ("lastName", Loggable[String].print(value.lastName)))
    }
  }
  
  implicit val loggableInstance: Loggable[User] = Loggable.instance { value => 
    val body = logSchemaInstance.schema(value)
     .map { case (prop, v) => prop + " = " + v }
     .mkString("(", ", ", ")")
     
    "User" + body
  }
}
 
case class Account(login: String, password: String, user: User) {

  val field: String = "something"

  def getterLikeDef(): Int = 123

}
  
object Account {
  import _root_.macrolog.{LogSchema, Loggable}
  import _root_.macrolog.Loggable._
  
  implicit val logSchemaInstance: LogSchema[Account] = new LogSchema[Account] {
    override def schema(value: Account): List[(String, String)] = {
      List(("login", Loggable[String].print(value.login)), ("user", Loggable[User].print(value.user)), ("field", Loggable[String].print(value.field)), ("getterLikeDef", Loggable[Int].print(value.getterLikeDef())))
    }
  }
  
  implicit val loggableInstance: Loggable[Account] = Loggable.instance { value => 
    val body = logSchemaInstance.schema(value)
      .map { case (prop, v) => prop + " = " + v }
      .mkString("(", ", ", ")")
      
    "Account" + body
  }
}
```
