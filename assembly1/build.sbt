import com.typesafe.sbt.packager.Keys._

val Version = "0.1-SNAPSHOT"

lazy val settings = Seq(
  organization := "org.tmt",
  version := Version,
  scalaVersion := "2.11.5"
)
lazy val packageSettings = settings ++ packagerSettings ++ packageArchetype.java_application

val hcd2 = "org.tmt" %% "hcd2" % Version

lazy val assembly1 = (project in file("."))
  .settings(packageSettings: _*)
  .settings(bashScriptExtraDefines ++= Seq("addJava -Dcsw.extjs.root=" + file("../../csw-extjs").absolutePath))
  .settings(
    libraryDependencies ++= Seq(hcd2)
  )
