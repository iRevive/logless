import sbt.Keys.organization
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._
import _root_.bintray.BintrayKeys._

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
  releaseVersionBump := sbtrelease.Version.Bump.Next,

  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    ReleaseStep(releaseStepTask(publish in bintray)),
    setNextVersion,
    commitNextVersion,
    pushChanges
  )
)