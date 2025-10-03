// cross
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "1.3.2")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.3.2")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"                   % "1.20.1")
addSbtPlugin("org.scala-native"   % "sbt-scala-native"              % "0.5.8")

// docs
addSbtPlugin("org.scalameta"      % "sbt-mdoc"                      % "2.7.2")

// publish
addSbtPlugin("com.github.sbt"     % "sbt-ci-release"                % "1.11.2")