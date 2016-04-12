package csw.pkgDemo.assembly1

import csw.services.loc.Connection.AkkaConnection
import csw.services.loc.ConnectionType.{AkkaType, HttpType}
import csw.services.loc.{Connection, _}
import csw.services.pkg.Supervisor
import csw.services.pkg.Component.{AssemblyInfo, RegisterAndTrackServices, RegisterOnly}

/**
 * Starts Assembly1 as a standalone application (as an alternative to starting it as part of Container1).
 */
object Assembly1App extends App {
  LocationService.initInterface()
  val assemblyName = "Assembly-1"
  val prefix = "" // prefix is only important if using a distributor actor
  val className = "csw.pkgDemo.assembly1.Assembly1"
  val componentId = ComponentId(assemblyName, ComponentType.Assembly)
  val hcd2a = AkkaConnection(ComponentId("HCD-2A", ComponentType.HCD))
  val hcd2b = AkkaConnection(ComponentId("HCD-2B", ComponentType.HCD))
  val hcdConnections: Set[Connection] = Set(hcd2a, hcd2b)
  val assemblyInfo = AssemblyInfo(assemblyName, prefix, className, RegisterAndTrackServices, Set(AkkaType, HttpType), hcdConnections)
  val supervisor = Supervisor(assemblyInfo)

}
