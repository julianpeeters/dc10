val CatsV = "2.10.0"
val Fs2V = "3.9.2"
val MUnitV = "0.7.29"
val SourcePosV = "1.1.0"

inThisBuild(List(
  crossScalaVersions := Seq(scalaVersion.value),
  description := "Scala code generation tools.",
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
  scalaVersion := "3.3.1",
  versionScheme := Some("semver-spec"),
))

lazy val dc10 = (project in file("."))
  .settings(name := "dc10")
  .aggregate(`dc10-compile`, `dc10-io`, `dc10-scala`)

lazy val `dc10-compile` = (project in file("modules/compile"))
  .settings(
    name := "dc10-compile",
  )

lazy val `dc10-io` = (project in file("modules/io"))
  .dependsOn(`dc10-compile`)
  .settings(
    name := "dc10-io",
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-io" % Fs2V
    )
  )

lazy val `dc10-scala` = (project in file("modules/scala"))
  .dependsOn(`dc10-compile`)
  .settings(
    name := "dc10-scala",
    libraryDependencies ++= Seq(
      // main
      "org.tpolecat"  %% "sourcepos" % SourcePosV,
      "org.typelevel" %% "cats-core" % CatsV,
      "org.typelevel" %% "cats-free" % CatsV,
      // test
      "org.scalameta" %% "munit" % MUnitV % Test
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
  .dependsOn(`dc10-compile`, `dc10-io`, `dc10-scala`)
  .enablePlugins(MdocPlugin)
  .enablePlugins(NoPublishPlugin)