// cross
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "1.3.2")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.3.2")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"                   % "1.15.0")
addSbtPlugin("org.scala-native"   % "sbt-scala-native"              % "0.4.17")

// docs
addSbtPlugin("org.scalameta"      % "sbt-mdoc"                      % "2.5.2")

// publish
addSbtPlugin("com.github.sbt"     % "sbt-ci-release"                % "1.5.12")
addSbtPlugin("org.typelevel"      % "sbt-typelevel-no-publish"      % "0.6.6")