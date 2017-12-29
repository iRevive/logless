import sbt.Keys.organization

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(releaseSettings: _*)
  .settings(macroAnnotationSettings: _*)
  .settings(
    name := Settings.name,
    libraryDependencies ++= Dependencies.root
  )
  .aggregate(macros)
  .dependsOn(macros)

lazy val macros = (project in file("macros"))
  .settings(commonSettings: _*)
  .settings(releaseSettings: _*)
  .settings(macroAnnotationSettings: _*)
  .settings(
    name := s"${Settings.name}-macros",
    libraryDependencies ++= Dependencies.macros
  )

lazy val commonSettings = Seq(
  organization := Settings.organization,

  scalaVersion := Version.scala,

  resolvers ++= List(Resolver.sonatypeRepo("releases")),

 // crossScalaVersions := Seq("2.11.11", "2.12.1"),

  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-encoding", "UTF-8",
    "-Xfuture",
    "-Xlint:unsound-match", // Pattern match may not be typesafe.
    "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
    "-Ypartial-unification", // Enable partial unification in type constructor inference
    "-Ywarn-dead-code", // Warn when dead code is identified.
    "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
    "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
    "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
    "-Ywarn-unused", // Warn if a method or value is unused.
    "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
  ),

  licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
)

lazy val macroAnnotationSettings = Seq(
  resolvers += Resolvers.scalameta,
  addCompilerPlugin("org.scalameta" % "paradise" % Version.scalametaParadise cross CrossVersion.full),
  scalacOptions += "-Xplugin-require:macroparadise",
  scalacOptions in (Compile, console) ~= (_ filterNot (_ contains "paradise"))
)

lazy val releaseSettings = Seq(
  sources in(Compile, doc) := Seq.empty,
  publishArtifact in(Compile, packageDoc) := false
)