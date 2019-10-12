name := "sgit_Delton"

version := "0.1"

scalaVersion := "2.13.1"

parallelExecution in Test := false

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.8"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"
libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.8.0"
libraryDependencies += "com.outr" %% "hasher" % "1.2.2"