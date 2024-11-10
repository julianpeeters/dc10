val Fs2V = "3.11.0"
val CatsV = "2.12.0"

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
    "-Xkind-projector:underscores"
  ),
  scalaVersion := "3.5.2",
  versionScheme := Some("semver-spec"),
  sonatypeCredentialHost := xerial.sbt.Sonatype.sonatypeCentralHost
))

lazy val dc10 = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("."))
  .settings(name := "dc10")
  .aggregate(`dc10-core`, `dc10-io`)

lazy val `dc10-core` = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("modules/core"))
  .settings(
    name := "dc10-core",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-core" % CatsV,
    )  
  )

lazy val `dc10-io` = crossProject(JVMPlatform)
  .in(file("modules/io"))
  .dependsOn(`dc10-core`)
  .settings(
    name := "dc10-io",
    libraryDependencies ++= Seq(
      "co.fs2" %%% "fs2-io" % Fs2V
    )
  )

lazy val docs = project.in(file("docs/gitignored"))
  .settings(
    mdocOut := file("."),
    mdocVariables := Map(
      "SCALA" -> crossScalaVersions.value.map(e => e.takeWhile(_ != '.')).mkString(", "),
      "VERSION" -> version.value.takeWhile(_ != '+'),
    )
  )
  .dependsOn(`dc10-core`.jvm, `dc10-io`.jvm)
  .enablePlugins(MdocPlugin)
  .enablePlugins(NoPublishPlugin)