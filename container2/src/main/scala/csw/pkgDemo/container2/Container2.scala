package csw.pkgDemo.container2

import csw.services.apps.containerCmd.ContainerCmd

/**
 * Creates container2 based on resources/container2.conf
 */
object Container2 extends App {
  private val a = args // Required to avoid null args below
  ContainerCmd("container2", a, Some("container2.conf"))
}
