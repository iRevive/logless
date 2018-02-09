import sbt.Keys.organization

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(releaseSettings: _*)
  .settings(macroAnnotationSettings: _*)
  .settings(
    name := Settings.name,
    libraryDependencies ++= Dependencies.root
  )

lazy val commonSettings = Seq(
  organization := Settings.organization,

  scalaVersion := Version.scala,

  resolvers ++= List(Resolver.sonatypeRepo("releases")),

  //crossScalaVersions := Seq("2.11.11", "2.12.1"),

  publishMavenStyle := false,

  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-encoding", "UTF-8"
  ),

  licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
)

lazy val macroAnnotationSettings = Seq(
  resolvers += Resolvers.scalameta,
  addCompilerPlugin("org.scalameta" %% "paradise" % Version.scalametaParadise cross CrossVersion.full),
  scalacOptions += "-Xplugin-require:macroparadise",
  scalacOptions in (Compile, console) ~= (_ filterNot (_ contains "paradise"))
)

lazy val releaseSettings = Seq(
  sources in(Compile, doc) := Seq.empty,
  publishArtifact in(Compile, packageDoc) := false
)