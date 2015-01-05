val Version = "0.1-SNAPSHOT"

lazy val settings = Seq(
  organization := "org.tmt",
  version := Version,
  scalaVersion := "2.11.4"
)

val containerCmd = "org.tmt" %% "containercmd" % Version
val cmd = "org.tmt" %% "cmd" % Version
val container1 = "org.tmt" %% "container1" % Version

lazy val root = (project in file(".")).
  settings(settings: _*).
  settings(
    name := "container1x",
    libraryDependencies ++= Seq(containerCmd, cmd, container1)
  )
