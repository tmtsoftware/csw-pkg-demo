package csw.pkgDemo.container1

import csw.services.apps.containerCmd.ContainerCmd

/**
 * Creates container1 based on resources/container1.conf
 */
object Container1 extends App {
  // This defines the names that can be used with the --start option and the config files used ("" is the default entry)
  val m = Map(
    "" -> "container1.conf" // default value
  )
  ContainerCmd("container1", args, m)
}
