val Version = "0.1-SNAPSHOT"

lazy val settings = Seq(
  organization := "org.tmt",
  version := Version,
  scalaVersion := "2.11.4"
)

val containerCmd = "org.tmt" %% "containercmd" % Version
val container2 = "org.tmt" %% "container2" % Version

lazy val root = (project in file(".")).
  settings(settings: _*).
  settings(
    name := "containerx",
    libraryDependencies ++= Seq(containerCmd, container2)
  )
