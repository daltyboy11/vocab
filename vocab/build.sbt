scalaVersion := "2.13.1"
scalacOptions += "-deprecation"
scalacOptions += "-explaintypes"
scalacOptions += "-feature"
scalacOptions += "-Xlint"

// ============================================================================

name := "vocab"
organization := "io.daltyboy11.github"
version := "1.0.0"

// Want to use a published library in your project?
// You can define other libraries as dependencies in your build like this:
libraryDependencies += "org.scalatest" % "scalatest_2.13" % "3.1.0" % "test"
