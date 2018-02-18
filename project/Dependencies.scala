import sbt._

object Settings {
  val organization          = "io.github.irevive"
  val name                  = "macrolog"
}

object Version {
  val scala                 = "2.12.4"

  val simulacrum            = "0.11.0"
  val scalaLogging          = "3.7.2"
  val logback               = "1.2.3"

  val scalatest             = "3.0.5"
  val scalamock             = "4.1.0"

  val scalameta             = "1.8.0"
  val scalametaParadise     = "3.0.0-M10"
}

object Resolvers {
  val scalameta = Resolver.url("scalameta", url("http://dl.bintray.com/scalameta/maven"))(Resolver.ivyStylePatterns)
  val irevive   = Resolver.url("irevive", url("http://dl.bintray.com/irevive/maven"))(Resolver.ivyStylePatterns)
}

object Dependencies {

  val root = List(
    "org.scala-lang"              % "scala-reflect"             % Version.scala,
    "org.scalameta"               %% "scalameta"                % Version.scalameta,
    "com.typesafe.scala-logging"  %% "scala-logging"            % Version.scalaLogging,
    "com.github.mpilquist"        %% "simulacrum"               % Version.simulacrum,
    "ch.qos.logback"              % "logback-classic"           % Version.logback,
    "org.scalatest"               %% "scalatest"                % Version.scalatest         % Test,
    "org.scalamock"               %% "scalamock"                % Version.scalamock         % Test,
    "org.scalameta"               %% "testkit"                  % Version.scalameta         % Test
  )

}