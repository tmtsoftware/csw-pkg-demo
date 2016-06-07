package csw.pkgDemo.assembly1

import akka.actor.ActorRef
import csw.pkgDemo.hcd2.Hcd2
import csw.services.ccs.AssemblyController
import csw.services.loc.LocationService.Location
import csw.services.pkg.Component.AssemblyInfo
import csw.services.pkg.{Assembly, LifecycleHandler, Supervisor}
import csw.util.config.StateVariable.CurrentState
import csw.util.config.Configurations.SetupConfigArg

/**
 * A test assembly that just forwards configs to HCDs based on prefix
 *
 * @param info contains information about the assembly and the components it depends on
 */
case class Assembly1(info: AssemblyInfo)
    extends Assembly with AssemblyController with LifecycleHandler {

  import AssemblyController._
  import Supervisor._

  // Holds the current HCD states, used to answer requests
  var stateMap = Map[String, CurrentState]()

  // Starts the assembly
  lifecycle(supervisor)

  // Get the connections to the HCDs this assembly uses and track them
  trackConnections(info.connections)

  // Receive messages
  override def receive: Receive = controllerReceive orElse lifecycleHandlerReceive orElse {
    // Current state received from one of the HCDs
    case s: CurrentState ⇒ updateCurrentState(s)

    case x               ⇒ log.error(s"Unexpected message: $x")
  }

  // Current state received from one of the HCDs: For now just forward it to any subscribers.
  // It might make more sense to create an Assembly state, built from the various HCD states and
  // publish that to the subscribers... TODO
  private def updateCurrentState(s: CurrentState): Unit = {
    notifySubscribers(s)
    stateMap += s.prefix → s
  }

  // For now, when the current state is requested, send the HCD states.
  // TODO: Use assembly specific state
  override protected def requestCurrent(): Unit = {
    stateMap.values.foreach(notifySubscribers)
  }

  /**
   * Validates a received config arg. In this case, we just check that each config
   * contains either a filter or a disperser key.
   */
  private def validate(config: SetupConfigArg): Validation = {
    val result: Seq[Validation] = config.configs.map { c ⇒
      if (c.exists(Hcd2.filterKey) || c.exists(Hcd2.disperserKey)) Valid
      else Invalid("Expected a filter or disperser config")
    }
    result.find(!_.isValid).getOrElse(Valid)
  }

  override protected def setup(locationsResolved: Boolean, configArg: SetupConfigArg,
                               replyTo: Option[ActorRef]): Validation = {
    val valid = validate(configArg)
    if (valid.isValid) {
      // The call below just distributes the configs to the HCDs based on matching prefix,
      // but you could just as well generate new configs and send them here...
      distributeSetupConfigs(locationsResolved, configArg, replyTo)
    } else valid
  }

  /**
   * Called when all HCD locations are resolved.
   * Overridden here to subscribe to status values from the HCDs.
   */
  override protected def allResolved(locations: Set[Location]): Unit = {
    subscribe(locations)
  }
}
