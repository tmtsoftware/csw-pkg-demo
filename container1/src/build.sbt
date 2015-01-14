val Version = "0.1-SNAPSHOT"

lazy val settings = Seq(
  organization := "org.tmt",
  version := Version,
  scalaVersion := "2.11.4"
)

val containerCmd = "org.tmt" %% "containercmd" % Version
val assembly1 = "org.tmt" %% "assembly1" % Version

lazy val root = (project in file(".")).
  settings(settings: _*).
  settings(
    libraryDependencies ++= Seq(containerCmd, assembly1)
  )
