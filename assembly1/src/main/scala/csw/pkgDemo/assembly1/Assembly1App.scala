package csw.pkgDemo.assembly1

import csw.services.loc.{ServiceType, ServiceId}
import csw.services.pkg.Component

/**
 * Starts Assembly1 as a standalone application.
 */
object Assembly1App extends App {
  val name = "Assembly-1"
  val prefix = "" // prefix is only important if using a distributor actor
  val props = Assembly1.props(name)
  val serviceId = ServiceId(name, ServiceType.Assembly)
  val httpUri = None
  val services = List(
    ServiceId("HCD-2A", ServiceType.HCD),
    ServiceId("HCD-2B", ServiceType.HCD)
  )
  Component.create(props, serviceId, prefix, services)
}
