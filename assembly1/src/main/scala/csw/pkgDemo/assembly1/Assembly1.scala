package csw.pkgDemo.assembly1

import akka.actor.{ActorRef, Props}
import csw.services.ccs.{AssemblyController, HcdController}
import csw.services.loc.LocationService.{Location, ResolvedAkkaLocation}
import csw.services.pkg.Component.AssemblyInfo
import csw.services.pkg.{Assembly, LifecycleHandler, Supervisor}
import csw.util.akka.PublisherActor
import csw.util.cfg.Configurations.StateVariable.{CurrentState, DemandState}
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
      if (locationsResolved) {
        // The code below just distributes the configs to the HCDs based on matching prefix,
        // but you could just as well generate new configs and send them here...
        val pairs = for {
          config ← configArg.configs
          actorRef ← getActorRefs(config.prefix)
        } yield {
          actorRef ! HcdController.Submit(config)
          (actorRef, DemandState(config))
        }
        val hcds = pairs.map(_._1).toSet
        val demandStates = pairs.map(_._2)
        matchDemandStates(demandStates, hcds, replyTo, configArg.info.runId)
      } else log.error(s"Unresolved locations for one or more HCDs")
    }
    valid
  }

  /**
   * Called when the HCD locations are resolved.
   * Override here so we can subscribe to status values from the HCD.
   */
  override protected def allResolved(locations: Set[Location]): Unit = {
    val x = locations.collect {
      case r @ ResolvedAkkaLocation(connection, uri, prefix, actorRefOpt) ⇒ actorRefOpt
    }
    val hcds = x.flatten
    hcds.foreach(_ ! PublisherActor.Subscribe)
  }
}
