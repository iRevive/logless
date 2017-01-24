val buildSettings = Defaults.coreDefaultSettings ++ Seq(
  version := "0.1",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-encoding", "UTF-8", "-Xplugin-require:macroparadise"),
  scalaVersion := Version.scala,
  sources in(Compile, doc) := Seq.empty,
  publishArtifact in(Compile, packageDoc) := false,
  resolvers += Resolvers.scalaMeta,
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  addCompilerPlugin(Library.scalaMetaParadise)
)

lazy val macros = Project(
  "macros",
  file("macros"),
  settings = buildSettings ++ Seq(
    name := "logless-macros",
    libraryDependencies ++= Dependencies.loglessMacro
  )
)

lazy val core = Project(
  "core",
  file("core"),
  settings = buildSettings ++ Seq(
    name := "logless-core",
    libraryDependencies ++= Dependencies.logless
  )
).aggregate(macros).dependsOn(macros)

lazy val root = project.in(file("."))
  .settings(buildSettings ++ Seq(
    name := "logless"
  ))