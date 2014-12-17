package csw.pkgDemo.container2

import com.typesafe.config.ConfigFactory
import csw.services.pkg.Container

/**
 * Creates container2 based on resources/container2.conf
 */
object Container2 extends App {
  Container.create(ConfigFactory.load("container2.conf"))
}
