import sbt._

object Version {
  val scala             = "2.11.8"
  val scalaTest         = "3.0.0"
  val scalaMeta         = "1.4.0.558"
  val scalaMetaParadise = "3.0.0.140"
  val scalaLogging      = "3.5.0"
  val config            = "1.3.1"
}

object Resolvers {
  val scalaMeta = Resolver.url("scalameta", url("http://dl.bintray.com/scalameta/maven"))(Resolver.ivyStylePatterns)
}

object Library {
  val scalaReflect          = "org.scala-lang"              % "scala-reflect"    % Version.scala
  val scalaTest             = "org.scalatest"               %% "scalatest"       % Version.scalaTest
  val scalaMeta             = "org.scalameta"               % "scalameta_2.11"   % Version.scalaMeta
  val scalaMetaParadise     = "org.scalameta"               % "paradise_2.11.8"  % Version.scalaMetaParadise
  val scalaLogging          = "com.typesafe.scala-logging"  %% "scala-logging"   % Version.scalaLogging
  val config                = "com.typesafe"                % "config"           % Version.config
}

object Dependencies {
  import Library._

  val loglessMacro = List(
    scalaReflect,
    scalaMeta,
    config
  )

  val logless = List(
    scalaLogging,

    scalaTest % "test"
  )

}