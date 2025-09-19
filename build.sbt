val Fs2V = "3.13.0-M7"
val CatsV = "2.13.0"

inThisBuild(List(
  crossScalaVersions := Seq(scalaVersion.value),
  description := "Code generation tools for Scala",
  organization := "com.julianpeeters",
  homepage := Some(url("https://github.com/julianpeeters/dc10")),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      "julianpeeters",
      "Julian Peeters",
      "julianpeeters@gmail.com",
      url("http://github.com/julianpeeters")
    )
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-Werror",
    "-Wunused:all",
  ),
  scalaVersion := "3.3.6",
  versionScheme := Some("semver-spec"),
))

lazy val dc10 = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("."))
  .settings(name := "dc10")
  .enablePlugins(NoPublishPlugin)
  .aggregate(core, io)

lazy val core = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("modules/core"))
  .settings(
    name := "dc10-core",
    libraryDependencies ++= Seq(
      "co.fs2"        %%% "fs2-io"    % Fs2V,
      "org.typelevel" %%% "cats-core" % CatsV,
    )  
  )

lazy val io = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("modules/io"))
  .settings(name := "dc10-io")
  .dependsOn(core)

lazy val docs = project.in(file("docs/gitignored"))
  .settings(
    mdocOut := file("."),
    mdocVariables := Map(
      "SCALA" -> crossScalaVersions.value.map(e => e.takeWhile(_ != '.')).mkString(", "),
      "VERSION" -> version.value.takeWhile(_ != '+'),
    )
  )
  .dependsOn(core.jvm)
  .enablePlugins(MdocPlugin)
  .enablePlugins(NoPublishPlugin)