name := "sgit_Delton"

version := "0.1"

//scalaVersion := "2.13.1"
scalaVersion := "2.12.10"


parallelExecution in Test := false

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.8"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"
libraryDependencies += "com.github.scopt" % "scopt_2.11" % "4.0.0-RC2"
libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.8.0"
