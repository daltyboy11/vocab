scalaVersion := "2.13.0"
scalacOptions += "-deprecation"
scalacOptions += "-explaintypes"
scalacOptions += "-feature"
scalacOptions += "-Xlint"

// ============================================================================

name := "vocab"
organization := "io.github.daltyboy11"
version := "1.0.0"

/* Unit testing */
libraryDependencies += "org.scalatest" % "scalatest_2.13" % "3.1.0" % "test"

/* File Management */
libraryDependencies += "io.github.soc" % "directories" % "11"
