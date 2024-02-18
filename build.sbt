val Fs2V = "3.9.2"
val SourcePosV = "1.1.0"

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
    "-source:future",
    "-Wunused:all",
    "-Wvalue-discard"
  ),
  scalaVersion := "3.4.0",
  versionScheme := Some("semver-spec"),
))

lazy val dc10 = (project in file("."))
  .settings(name := "dc10")
  .aggregate(`dc10-core`, `dc10-io`)

lazy val `dc10-core` = (project in file("modules/core"))
  .settings(
    name := "dc10-core",
  )

lazy val `dc10-io` = (project in file("modules/io"))
  .dependsOn(`dc10-core`)
  .settings(
    name := "dc10-io",
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-io" % Fs2V
    )
  )

lazy val docs = project.in(file("docs/gitignored"))
  .settings(
    mdocOut := dc10.base,
    mdocVariables := Map(
      "SCALA" -> crossScalaVersions.value.map(e => e.takeWhile(_ != '.')).mkString(", "),
      "VERSION" -> version.value.takeWhile(_ != '+'),
    )
  )
  .dependsOn(`dc10-core`, `dc10-io`)
  .enablePlugins(MdocPlugin)
  .enablePlugins(NoPublishPlugin)