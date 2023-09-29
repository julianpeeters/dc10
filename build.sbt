val CatsV = "2.9.0"
val Fs2V = "3.7.0"
val MUnitV = "0.7.29"
val SourcePosV = "1.1.0"

ThisBuild / description := "Purely functional Scala code generation."
ThisBuild / organization := "com.julianpeeters"
ThisBuild / scalaVersion := "3.3.1"
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
  .aggregate(`dc10-compile`, `dc10-io`, `dc10-scala`)

lazy val `dc10-compile` = (project in file("modules/compile"))
  .settings(
    commonSettings,
    name := "dc10-compile",
  )

lazy val `dc10-io` = (project in file("modules/io"))
  .dependsOn(`dc10-compile`)
  .settings(
    commonSettings,
    name := "dc10-io",
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-io" % Fs2V
    )
  )

lazy val `dc10-scala` = (project in file("modules/scala"))
  .dependsOn(`dc10-compile`)
  .settings(
    commonSettings,
    name := "dc10-scala",
    libraryDependencies ++= Seq(
      "org.tpolecat"  %% "sourcepos" % SourcePosV,
      "org.typelevel" %% "cats-core" % CatsV,
      "org.typelevel" %% "cats-free" % CatsV,
    )
  )