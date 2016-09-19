package csw.pkgDemo.hcd2

import akka.actor.ActorRef
import csw.services.ccs.HcdController
import csw.services.pkg.Component.HcdInfo
import csw.services.pkg.Supervisor3.{Initialized, Started}
import csw.services.pkg.{Hcd, LifecycleHandler}
import csw.util.config.Configurations.SetupConfig
import csw.util.config.StringKey

// A test HCD that is configured with the given name and config path
case class Hcd2(info: HcdInfo, supervisor: ActorRef) extends Hcd with HcdController with LifecycleHandler {
  private val worker = context.actorOf(Hcd2Worker.props(info.prefix))

  supervisor ! Initialized
  supervisor ! Started

  override def receive: Receive = controllerReceive orElse lifecycleHandlerReceive

  // Send the config to the worker for processing
  override protected def process(config: SetupConfig): Unit = {
    worker ! config
  }

  // Ask the worker actor to send us the current state (handled by parent trait)
  override protected def requestCurrent(): Unit = {
    worker ! Hcd2Worker.RequestCurrentState
  }
}

object Hcd2 {
  /**
   * The prefix for filter configs
   */
  val filterPrefix = "tcs.mobie.blue.filter"

  /**
   * The prefix for disperser configs
   */
  val disperserPrefix = "tcs.mobie.blue.disperser"

  /**
   * The key for filter values
   */
  val filterKey = StringKey("filter")

  /**
   * The key for disperser values
   */
  val disperserKey = StringKey("disperser")

  /**
   * The available filters
   */
  val FILTERS: Vector[String] = Vector[String]("None", "g_G0301", "r_G0303", "i_G0302", "z_G0304", "Z_G0322", "Y_G0323", "u_G0308")

  /**
   * The available dispersers
   */
  val DISPERSERS: Vector[String] = Vector[String]("Mirror", "B1200_G5301", "R831_G5302", "B600_G5303", "B600_G5307", "R600_G5304", "R400_G5305", "R150_G5306")
}

