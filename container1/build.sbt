import com.typesafe.sbt.packager.Keys._

val Version = "0.1-SNAPSHOT"

lazy val settings = Seq(
  organization := "org.tmt",
  version := Version,
  scalaVersion := "2.11.5"
)
lazy val packageSettings = settings ++ packagerSettings ++ packageArchetype.java_application

val containerCmd = "org.tmt" %% "containercmd" % Version
val assembly1 = "org.tmt" %% "assembly1" % Version

lazy val container1 = (project in file("."))
  .settings(packageSettings: _*)
  .settings(bashScriptExtraDefines ++= Seq("addJava -Dcsw.extjs.root=" + file("../../csw-extjs").absolutePath))
  .settings(
    libraryDependencies ++= Seq(containerCmd, assembly1)
  )
