package csw.pkgDemo.container1

import com.typesafe.config.ConfigFactory
import csw.services.pkg.Container

/**
 * Creates container1 based on resources/container1.conf
 */
object Container1 extends App {
  Container.create(ConfigFactory.load("container1.conf"))
}
