package csw.pkgDemo.assembly1

import csw.services.ls.LocationService.RegInfo
import csw.services.ls.LocationServiceActor.{ServiceType, ServiceId}
import csw.services.pkg.Component
import csw.services.pkg.LifecycleManager.Startup

/**
 * Starts Assembly1 as a standalone application.
 * Args: name, configPath
 */
object Assembly1App extends App {
  val name = "Assembly-1"
  val props = Assembly1.props(name)
  val serviceId = ServiceId(name, ServiceType.Assembly)
  val httpUri = None
  val regInfo = RegInfo(serviceId, None, httpUri)
  val services = List(
    ServiceId("HCD-2A", ServiceType.HCD),
    ServiceId("HCD-2B", ServiceType.HCD),
    ServiceId("HCD-2C", ServiceType.HCD),
    ServiceId("HCD-2D", ServiceType.HCD)
  )
  val info = Component.create(props, regInfo, services)
  info.lifecycleManager ! Startup
}
