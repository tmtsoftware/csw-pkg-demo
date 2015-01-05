package csw.pkgDemo.container2

import akka.actor.Props
import csw.services.cmd.akka.{ CommandServiceActor, OneAtATimeCommandQueueController }
import csw.services.pkg.{ LifecycleHandler, Hcd }

// A test HCD that is configured with the given name and config path
object Hcd2 {
  def props(name: String, configPath: String): Props = Props(classOf[Hcd2], name, configPath)
}

case class Hcd2(name: String, configPath: String) extends Hcd
    with CommandServiceActor
    with OneAtATimeCommandQueueController
    with LifecycleHandler {

  println("XXXXXXXXXXXX Here Here XXXXXX")

  val configKey = configPath.split('.').last
  override val configActor = context.actorOf(TestConfigActor.props(commandStatusActor, configKey, 3), name)

  override def receive: Receive = receiveCommands orElse receiveLifecycleCommands orElse {
    case x ⇒ log.error(s"XXX HCD2: Received unknown message: $x from ${sender()}")
  }
}
