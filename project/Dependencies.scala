import sbt._

// Dependencies

object Dependencies {

  val scalaVersion = "2.11.4"
  val akkaVersion = "2.3.7"

  def compile   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def provided  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
  def runtime   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
  def container (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

  val akkaActor      = "com.typesafe.akka"             %% "akka-actor"            % akkaVersion
  val akkaRemote     = "com.typesafe.akka"             %% "akka-remote"           % akkaVersion

  val jeromq         = "org.zeromq"                     % "jeromq"                % "0.3.3"

  // csw packages (installed with sbt publish-local)
  val pkg            = "org.tmt"                       %% "pkg"                     % Settings.Version
  val containerCmd   = "org.tmt"                       %% "containercmd"            % Settings.Version
  val log            = "org.tmt"                       %% "log"                     % Settings.Version

}

