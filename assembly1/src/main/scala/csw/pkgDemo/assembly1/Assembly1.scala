package csw.pkgDemo.assembly1

import akka.actor.{ ActorRef, Props }
import com.typesafe.config.{ ConfigFactory, Config }
import csw.services.ccs.AssemblyController.{ Validation, Valid }
import csw.services.ccs.{ HcdController, StateMatcherActor, AssemblyController }
import csw.services.loc.AccessType.AkkaType
import csw.services.loc.LocationService.ResolvedService
import csw.services.loc.ServiceRef
import csw.services.pkg.{ Assembly, LifecycleHandler }
import csw.util.cfg.Configurations.StateVariable.DemandState
import csw.util.cfg.Configurations.SetupConfigArg

object Assembly1 {
  def props(name: String, config: Config = ConfigFactory.empty()): Props = Props(classOf[Assembly1], name, config)
}

/**
 * A test assembly that just forwards configs to HCDs based on prefix
 * @param name the name of the asembly
 * @param config config file with settings for the assembly
 */
case class Assembly1(name: String, config: Config)
    extends Assembly with AssemblyController with LifecycleHandler {

  log.info(s"XXX application-name = ${System.getProperty("application-name")}")

  // Actor waiting for current state variable to match demand state
  private var stateMatcherActor: Option[ActorRef] = None

  /**
   * Validates a received config arg
   */
  private def validate(config: SetupConfigArg): Validation = {
    // TODO: add code to check if the config is valid
    Valid
  }

  /**
   * Called to process the setup config and reply to the given actor with the command status.
   *
   * @param services contains information about any required services
   * @param configArg contains a list of setup configurations
   * @param replyTo if defined, the actor that should receive the final command status.
   */
  override protected def setup(services: Map[ServiceRef, ResolvedService], configArg: SetupConfigArg,
                               replyTo: Option[ActorRef]): AssemblyController.Validation = {
    val valid = validate(configArg)
    if (valid.isValid) {
      // The code below just distributes the configs to the HCDs based on matching prefix,
      // but you could just as well generate new configs and send them here...
      val demandStates = for {
        config ← configArg.configs
        service ← services.values.find(v ⇒ v.prefix == config.prefix && v.serviceRef.accessType == AkkaType)
        actorRef ← service.actorRefOpt
      } yield {
        actorRef ! HcdController.Submit(config)
        DemandState(config)
      }
      matchDemandStates(demandStates, replyTo, configArg.info.runId)
    }
    valid
  }
}
