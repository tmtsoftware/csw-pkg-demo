package csw.pkgDemo.container1

import akka.actor.Props
import csw.services.pkg.Assembly
import csw.services.cmd.akka.{ AssemblyCommandServiceActor, OneAtATimeCommandQueueController }
import csw.services.cmd.akka.CommandQueueActor.SubmitWithRunId
import java.util.Date
import csw.services.ls.LocationServiceActor.{ ServiceType, ServiceId }
import csw.services.cmd.spray.CommandServiceHttpServer
import csw.util.cfg.Configurations.SetupConfigList

object Assembly1 {
  def props(name: String): Props = Props(classOf[Assembly1], name)
}

// A test assembly
case class Assembly1(name: String)
    extends Assembly with AssemblyCommandServiceActor with OneAtATimeCommandQueueController {

  // Register with the location service (which must be started as a separate process)
  registerWithLocationService(ServiceId(name, ServiceType.Assembly))

  override def receive: Receive = receiveComponentMessages orElse receiveCommands

  // Define the HCDs we want to use
  val serviceIds = List(
    ServiceId("HCD-2A", ServiceType.HCD),
    ServiceId("HCD-2B", ServiceType.HCD),
    ServiceId("HCD-2C", ServiceType.HCD),
    ServiceId("HCD-2D", ServiceType.HCD))
  requestServices(serviceIds)
  startHttpServer()

  // Starts the Spray HTTP server for accessing this assembly via HTTP
  def startHttpServer(): Unit = {
    // Start a HTTP server with the REST interface
    val interface = Container1Settings(context.system).interface
    val port = Container1Settings(context.system).port
    val timeout = Container1Settings(context.system).timeout
    context.actorOf(CommandServiceHttpServer.props(self, interface, port, timeout), "commandService")
  }

  /**
   * Called when a command is submitted
   * @param s holds the config, runId and sender
   */
  override def submit(s: SubmitWithRunId): Unit = {
    log.debug(s"Submit with runId(${s.runId}) ${s.config}")

    // Test changing the contents of the config
    val confs = for (config ← s.config.asInstanceOf[SetupConfigList])
      yield if (config.prefix == "tmt.mobie.blue.filter") {
      config.withValues("timestamp" -> new Date().getTime)
    } else {
      config
    }
    commandQueueActor ! SubmitWithRunId(confs, s.submitter, s.runId)
  }

  def initialize(): Unit = { log.info("Assembly1 initialize") }

  def startup(): Unit = { log.info("Assembly1 startup") }

  def run(): Unit = { log.info("Assembly1 run") }

  def shutdown(): Unit = { log.info("Assembly1 shutdown") }

  def uninit(): Unit = { log.info("Assembly1 uninit") }

  def remove(): Unit = { log.info("Assembly1 remove") }
}
