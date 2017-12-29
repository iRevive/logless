import sbt._

object Settings {
  val organization          = "io.github.irevive"
  val name                  = "macrolog"
}

object Version {
  val scala                 = "2.12.1"

  val simulacrum            = "0.10.0"
  val scalaLogging          = "3.5.0"
  val logback               = "1.2.3"

  val scalatest             = "3.0.4"
  val scalamock             = "4.0.0"

  val scalameta             = "1.8.0"
  val scalametaParadise     = "3.0.0-M8"
}

object Resolvers {
  val scalameta = Resolver.url("scalameta", url("http://dl.bintray.com/scalameta/maven"))(Resolver.ivyStylePatterns)
  val irevive   = Resolver.url("irevive", url("http://dl.bintray.com/irevive/maven"))(Resolver.ivyStylePatterns)
}

object Dependencies {

  val macros = List(
    "org.scala-lang"              % "scala-reflect"           % Version.scala,
    "org.scalameta"               %% "scalameta"              % Version.scalameta,
    "org.scalatest"               %% "scalatest"              % Version.scalatest         % Test,
    "org.scalameta"               %% "testkit"                % Version.scalameta         % Test
  )

  val root = List(
    "com.typesafe.scala-logging"  %% "scala-logging"          % Version.scalaLogging,
    "com.github.mpilquist"        %% "simulacrum"             % Version.simulacrum,
    "ch.qos.logback"              % "logback-classic"         % Version.logback,
    "org.scalatest"               %% "scalatest"              % Version.scalatest         % Test,
    "org.scalamock"               %% "scalamock"              % Version.scalamock         % Test
  )

}