import sbt._

object Settings {
  val organization          = "io.github.irevive"
  val name                  = "macrolog"
}

object Version {
  val scala                 = "2.12.1"

  val scalaLogging          = "3.5.0"
  val scalaTest             = "3.0.0"
  val simulacrum            = "0.10.0"
  val cats                  = "0.9.0"
  val logback               = "1.2.3"

  val scalaMeta             = "1.8.0"
  val scalaMetaParadise     = "3.0.0-M7"
}

object Resolvers {
  val scalaMeta = Resolver.url("scalameta", url("http://dl.bintray.com/scalameta/maven"))(Resolver.ivyStylePatterns)
  val irevive   = Resolver.url("irevive", url("http://dl.bintray.com/irevive/maven"))(Resolver.ivyStylePatterns)
}

object Library {
  val scalaReflect          = "org.scala-lang" % "scala-reflect" % Version.scala
  val scalaMeta             = "org.scalameta"  %% "scalameta"    % Version.scalaMeta
  val scalaMetaParadise     = "org.scalameta"  % "paradise"      % Version.scalaMetaParadise cross CrossVersion.full
}

object Dependencies {
  import Library._

  val macros = List(
    scalaReflect,
    scalaMeta
  )

  val root = Seq(
    "com.typesafe.scala-logging"  %% "scala-logging"   % Version.scalaLogging,
    "com.github.mpilquist"        %% "simulacrum"      % Version.simulacrum,
    "org.typelevel"               %% "cats"            % Version.cats,
    "ch.qos.logback"              % "logback-classic"  % Version.logback,
    "org.scalatest"               %% "scalatest"       % Version.scalaTest    % Test
  )

}