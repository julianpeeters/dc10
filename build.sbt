val CatsV = "2.9.0"
val Fs2V = "3.7.0"
val MonocleV = "3.1.0"
val MUnitV = "0.7.29"
val SourcePosV = "1.1.0"

ThisBuild / description := "A simplified AST for Scala code generation."
ThisBuild / organization := "com.julianpeeters"
ThisBuild / scalaVersion := "3.3.0"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / versionScheme := Some("semver-spec")

lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-Werror",
    "-source:future",
    "-Wunused:all",
    "-Wvalue-discard"
  ),
  libraryDependencies ++= Seq(
    "org.scalameta" %% "munit" % MUnitV % Test
  )
)

lazy val dc10 = (project in file("."))
  .settings(name := "dc10")
  .aggregate(`dc10-core`, `dc10-dsl`, `dc10-io`)

lazy val `dc10-core` = (project in file("modules/dc10-core"))
  .settings(
    commonSettings,
    name := "dc10-core",
    libraryDependencies ++= Seq(
      "org.tpolecat"  %% "sourcepos" % SourcePosV,
      "org.typelevel" %% "cats-core" % CatsV,
      "org.typelevel" %% "cats-free" % CatsV,
    )
  )

lazy val `dc10-dsl` = (project in file("modules/dc10-dsl"))
  .dependsOn(`dc10-core`)
  .settings(
    commonSettings,
    name := "dc10-dsl",
  )

lazy val `dc10-io` = (project in file("modules/dc10-io"))
  .dependsOn(`dc10-core`)
  .settings(
    name := "dc10-io",
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-io" % Fs2V
    )
  )