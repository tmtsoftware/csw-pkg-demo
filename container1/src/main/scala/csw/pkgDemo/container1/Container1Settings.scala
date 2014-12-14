package csw.pkgDemo.container1

import akka.actor.{ Extension, ExtendedActorSystem, ExtensionKey }

import scala.concurrent.duration._

/**
 * The settings for Container1
 */
object Container1Settings extends ExtensionKey[Container1Settings]

class Container1Settings(system: ExtendedActorSystem) extends Extension {

  // The network interface the service gets bound to, e.g. `"localhost"`.
  val interface: String = system.settings.config getString "csw.test.assembly1-http.interface"

  // The port the service gets bound to, e.g. `8080`.
  val port: Int = system.settings.config getInt "csw.test.assembly1-http.port"

  // The amount of time to wait when polling for the command status
  val timeout: FiniteDuration = Duration(system.settings.config.getDuration("csw.test.assembly1-http.timeout", MILLISECONDS),
    MILLISECONDS)
}
