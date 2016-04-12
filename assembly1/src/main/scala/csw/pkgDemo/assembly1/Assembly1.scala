package csw.pkgDemo.assembly1

import akka.actor.{ActorRef, Props}
import csw.services.ccs.{AssemblyController, HcdController}
import csw.services.pkg.Component.AssemblyInfo
import csw.services.pkg.{Assembly, LifecycleHandler, Supervisor}
import csw.util.cfg.Configurations.StateVariable.DemandState
import csw.util.cfg.Configurations.SetupConfigArg

object Assembly1 {
  /**
   * Can be used to create the Assembly1 actor
   */
  def props(info: AssemblyInfo): Props = Props(classOf[Assembly1], info)
}

/**
 * A test assembly that just forwards configs to HCDs based on prefix
 *
 * @param info contains information about the assembly and the components it depends on
 */
case class Assembly1(info: AssemblyInfo)
    extends Assembly with AssemblyController with LifecycleHandler {

  import AssemblyController._
  import Supervisor._

  // Starts the assembly
  lifecycle(supervisor)

  // Get the connections to the HCDs this assembly uses and track them
  trackConnections(info.connections)

  // Receive messages
  override def receive: Receive = controllerReceive orElse lifecycleHandlerReceive orElse {
    case x ⇒ log.error(s"Unexpected message: $x")
  }

  /**
   * Validates a received config arg
   */
  private def validate(config: SetupConfigArg): Validation = {
    // TODO: add code to check if the config is valid
    Valid
  }

  override protected def setup(locationsResolved: Boolean, configArg: SetupConfigArg,
                               replyTo: Option[ActorRef]): Validation = {
    val valid = validate(configArg)
    if (valid.isValid) {
      // The code below just distributes the configs to the HCDs based on matching prefix,
      // but you could just as well generate new configs and send them here...
      val demandStates = for {
        config ← configArg.configs
        actorRef ← getActorRefs(config.prefix)
      } yield {
        actorRef ! HcdController.Submit(config)
        DemandState(config)
      }
      matchDemandStates(demandStates, replyTo, configArg.info.runId)
    }
    valid
  }
}
