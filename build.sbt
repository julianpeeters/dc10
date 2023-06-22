val MUnitV = "0.7.29"

ThisBuild / description := "A simplified AST for Scala code generation."
ThisBuild / organization := "com.julianpeeters"
ThisBuild / scalaVersion := "3.3.0"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / versionScheme := Some("semver-spec")

lazy val dc10 = (project in file("."))
  .settings(name := "dc10")
  .aggregate(`dc10-core`)

lazy val `dc10-core` = (project in file("modules/dc10-core"))
  .settings(
    name := "dc10-core",
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % MUnitV % Test
    )
  )