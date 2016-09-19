package csw.pkgDemo.assembly1

import akka.actor.ActorRef
import csw.pkgDemo.hcd2.Hcd2
import csw.services.ccs.AssemblyController
import csw.services.loc.LocationService.Location
import csw.services.pkg.Component.AssemblyInfo
import csw.services.pkg.Supervisor3._
import csw.services.pkg.{Assembly, LifecycleHandler}
import csw.util.config.StateVariable._
import csw.util.config.Configurations.SetupConfigArg

/**
 * A test assembly that just forwards configs to HCDs based on prefix
 *
 * @param info contains information about the assembly and the components it depends on
 */
case class Assembly1(info: AssemblyInfo, supervisor: ActorRef)
    extends Assembly with AssemblyController with LifecycleHandler {

  import AssemblyController._

  // Holds the current HCD states, used to answer requests
  private var stateMap = Map[String, CurrentState]()

  // Starts the assembly
  supervisor ! Initialized
  supervisor ! Started

  // Get the connections to the HCDs this assembly uses and track them
  trackConnections(info.connections)

  // Receive messages
  override def receive: Receive = controllerReceive orElse lifecycleHandlerReceive orElse {
    // Current state received from one of the HCDs
    case s: CurrentState ⇒ updateCurrentState(s)

    case Running =>
      log.info("Received running")
    case RunningOffline =>
      log.info("Received running offline")
    case DoRestart =>
      log.info("Received dorestart")
    case DoShutdown =>
      log.info("Received doshutdown")
      // Just say complete for now
      supervisor ! ShutdownComplete
    case LifecycleFailureInfo(state: LifecycleState, reason: String) =>
      log.info(s"Received failed state: $state for reason: $reason")

    case x => log.error(s"Unexpected message: $x")
  }

  // Current state received from one of the HCDs: Send it, together with the other states,
  // to the subscribers.
  private def updateCurrentState(s: CurrentState): Unit = {
    stateMap += s.prefix -> s
    requestCurrent()
  }

  override protected def requestCurrent(): Unit = {
    notifySubscribers(CurrentStates(stateMap.values.map(identity).toSeq))
  }

  /**
   * Validates a received config arg. In this case, we just check that each config
   * contains either a filter or a disperser key.
   */
  private def validate(config: SetupConfigArg): Validation = {
    val result: Seq[Validation] = config.configs.map { c =>
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
