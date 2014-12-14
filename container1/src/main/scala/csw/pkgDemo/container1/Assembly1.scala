package csw.pkgDemo.container1

import akka.actor.Props
import csw.services.pkg.{ LifecycleHandler, Assembly }
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
case class Assembly1(name: String) extends Assembly
    with AssemblyCommandServiceActor
    with OneAtATimeCommandQueueController
    with LifecycleHandler {

  // Register with the location service (which must be started as a separate process)
  registerWithLocationService(ServiceId(name, ServiceType.Assembly))

  override def receive: Receive = receiveCommands orElse receiveLifecycleCommands orElse {
    case x ⇒ println(s"XXX Assembly1: Received unknown message: $x")
  }

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
}
