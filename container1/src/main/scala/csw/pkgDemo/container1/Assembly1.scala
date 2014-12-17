package csw.pkgDemo.container1

import java.util.Date

import akka.actor.Props
import csw.services.cmd.akka.CommandQueueActor.SubmitWithRunId
import csw.services.cmd.akka.{ AssemblyCommandServiceActor, OneAtATimeCommandQueueController }
import csw.services.cmd.spray.CommandServiceHttpServer
import csw.services.pkg.{ Assembly, LifecycleHandler }
import csw.util.cfg.Configurations.SetupConfigList

object Assembly1 {
  def props(name: String): Props = Props(classOf[Assembly1], name)
}

// A test assembly
case class Assembly1(name: String) extends Assembly
    with AssemblyCommandServiceActor
    with OneAtATimeCommandQueueController
    with LifecycleHandler {

  override def receive: Receive = receiveCommands orElse receiveLifecycleCommands orElse {
    case x ⇒ log.error(s"Assembly1: Received unknown message: $x")
  }

  startHttpServer()

  // Starts the Spray HTTP server for accessing this assembly via HTTP
  def startHttpServer(): Unit = {
    // Start a HTTP server with the REST interface
    val interface = Container1Settings(context.system).interface
    val port = Container1Settings(context.system).port
    val timeout = Container1Settings(context.system).timeout
    context.actorOf(CommandServiceHttpServer.props(context.parent, interface, port, timeout), "commandService")
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
