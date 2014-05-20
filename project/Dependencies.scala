import sbt._

// Dependencies

object Dependencies {

  val scalaVersion = "2.11.0"
  val akkaVersion = "2.3.2"
  val sprayVersion = "1.3.1-20140423"

  def compile   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def provided  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
  def runtime   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
  def container (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

  val akkaActor      = "com.typesafe.akka"             %% "akka-actor"            % akkaVersion
  val akkaKernel     = "com.typesafe.akka"             %% "akka-kernel"           % akkaVersion
  val akkaRemote     = "com.typesafe.akka"             %% "akka-remote"           % akkaVersion

  val jeromq         = "org.zeromq"                     % "jeromq"                % "0.3.3"
  val typesafeConfig = "com.typesafe"                   % "config"                % "1.2.0"
  val scalaLogging   = "com.typesafe.scala-logging"    %% "scala-logging-slf4j"   % "2.1.2"
  val logback        = "ch.qos.logback"                 % "logback-classic"       % "1.1.1"

  val sprayCan       = "io.spray"                      %% "spray-can"             % sprayVersion
  val sprayClient    = "io.spray"                      %% "spray-client"          % sprayVersion
  val sprayRouting   = "io.spray"                      %% "spray-routing"         % sprayVersion
  val sprayJson      = "io.spray"                      %% "spray-json"            % "1.2.6"
  val sprayTestkit   = "io.spray"                      %% "spray-testkit"         % sprayVersion

  val jgit           = "org.eclipse.jgit"               % "org.eclipse.jgit"      % "3.3.2.201404171909-r"
  val scalaIoFile    = "com.github.scala-incubator.io" %% "scala-io-file"         % "0.4.3"

  // csw packages (installed with sbt publish-local)
  val pkg            = "org.tmt"                       %% "pkg"                     % "1.0"

}

