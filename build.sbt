import sbt.Keys.libraryDependencies
import sbtassembly.AssemblyPlugin.defaultUniversalScript
name := "sgit_Delton"

version := "0.1"

//scalaVersion := "2.13.1"
scalaVersion := "2.12.10"


parallelExecution in Test := false

//libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"
//libraryDependencies += "com.github.scopt" % "scopt_2.11" % "4.0.0-RC2"
//libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.8.0"
//

lazy val root = (project in file("."))
  .settings(
    name := "sgit_Delton",
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test",
      libraryDependencies += "com.github.scopt" % "scopt_2.11" % "4.0.0-RC2",
      libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.8.0"
  )

assemblyOption in assembly := (assemblyOption in assembly).value
  .copy(prependShellScript = Some(defaultUniversalScript(shebang = false)))

assemblyOutputPath in assembly := file(baseDirectory.value.getAbsolutePath+"/sgit.jar")

//assemblyJarName in assembly := s"${name.value}"