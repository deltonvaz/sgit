import sbt.Keys.libraryDependencies
import sbtassembly.AssemblyPlugin.defaultUniversalScript

ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.13.0"

lazy val root = (project in file("."))
  .settings(
    name := "sgit_Delton",
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test",
      libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.0-RC2",
      libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.8.0"
  )

//Generate executable
assemblyOption in assembly := (assemblyOption in assembly).value
  .copy(prependShellScript = Some(defaultUniversalScript(shebang = false)))

parallelExecution in Test := false

//Export to root dir
assemblyOutputPath in assembly := file(baseDirectory.value.getAbsolutePath+"/sgit")