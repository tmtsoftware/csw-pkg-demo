package csw.pkgDemo.container1

import csw.services.apps.containerCmd.ContainerCmd

/**
 * Creates container1 based on resources/container1.conf
 */
object Container1 extends App {
  private val a = args // Required to avoid null args below
  ContainerCmd("container1", a, Some("container1.conf"))
}
