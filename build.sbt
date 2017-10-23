import sbt.Keys.organization

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(releaseSettings: _*)
  .settings(
    name := Settings.name,
    libraryDependencies ++= Dependencies.root
  )
  .aggregate(macros)
  .dependsOn(macros)

lazy val macros = (project in file("macros"))
  .settings(commonSettings: _*)
  .settings(
    name := s"${Settings.name}-macros",
    libraryDependencies ++= Dependencies.macros
  )

lazy val commonSettings = Seq(
  organization := Settings.organization,
  scalaVersion := Version.scala,

  resolvers ++= List(Resolvers.scalaMeta, Resolver.sonatypeRepo("releases")),

  //crossScalaVersions := Seq("2.11.11", "2.12.3"),

  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-encoding", "UTF-8",
    "-Xplugin-require:macroparadise",
    "-Ywarn-dead-code",
    "-Ywarn-inaccessible",
    "-Ywarn-unused",
    "-Ywarn-unused-import"
  ),

  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  addCompilerPlugin(Library.scalaMetaParadise)
)


lazy val releaseSettings = Seq(
  sources in(Compile, doc) := Seq.empty,
  publishArtifact in(Compile, packageDoc) := false
)