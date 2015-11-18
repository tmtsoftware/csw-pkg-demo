package csw.pkgDemo.hcd2

import akka.actor._
import akka.pattern.ask
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import csw.services.kvs.{ StateVariableStore, KvsSettings }
import csw.util.cfg.Configurations.StateVariable.CurrentState
import csw.util.cfg.Configurations._
import csw.util.cfg.StandardKeys
import org.zeromq.ZMQ

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{ Failure, Success }

object Hcd2Worker {
  def props(prefix: String): Props = Props(classOf[Hcd2Worker], prefix)

  val settings = ConfigFactory.load("zmq")

  val FILTERS = Array[String]("None", "g_G0301", "r_G0303", "i_G0302", "z_G0304", "Z_G0322", "Y_G0323", "u_G0308")
  val DISPERSERS = Array[String]("Mirror", "B1200_G5301", "R831_G5302", "B600_G5303", "B600_G5307", "R600_G5304", "R400_G5305", "R150_G5306")
}

/**
 * An actor that does the work of matching a configuration
 */
class Hcd2Worker(prefix: String) extends Actor with ActorLogging {
  import Hcd2Worker._
  import context.dispatcher
  val zmqKey = prefix.split('.').last

  val (key, choices) = if (zmqKey == "filter")
    (StandardKeys.filter, FILTERS)
  else (StandardKeys.disperser, DISPERSERS)

  val url = settings.getString(s"zmq.$zmqKey.url")
  log.info(s"For $zmqKey: using ZMQ URL = $url")

  val zmqClient = context.actorOf(ZmqClient.props(url))

  val svs = StateVariableStore(KvsSettings(context.system))

  override def receive: Receive = {
    case s: SetupConfig ⇒ submit(s)
    case x              ⇒ log.error(s"Unexpected message $x")
  }

  /**
   * Called when a configuration is submitted
   */
  def submit(setupConfig: SetupConfig): Unit = {
    log.info("Sending message to ZMQ hardware simulation")

    svs.get(prefix).onComplete {
      case Success(currentStateOpt) => currentStateOpt match {
        case Some(currentState) => currentState.get(key).foreach(sendToZmq(_, setupConfig))
      }
      case Failure(ex) => sendToZmq(choices(0), setupConfig)
    }
  }

  def sendToZmq(currentValue: String, setupConfig: SetupConfig): Unit = {
    setupConfig.get(key).foreach { value ⇒
      // XXX TODO: Send a number to the ZMQ process, which is the number of filters
      // or dispersers between the current one and the new one, moving to the right and wrapping around
//      choices.indexOf(value), choices.indexOf(currentValue)   ...

      val zmqMsg = ByteString(s"$zmqKey=$value", ZMQ.CHARSET.name())

      ask(zmqClient, ZmqClient.Command(zmqMsg))(6 seconds) onComplete {
        case Success(reply: ByteString) ⇒
          val msg = reply.decodeString(ZMQ.CHARSET.name())
          log.info(s"ZMQ Message: $msg")
          // For this test, assume setupConfig is the current state of the device...
          svs.set(CurrentState(setupConfig))

        case Success(m) ⇒ // should not happen
          log.error(s"Unexpected reply from zmq: $m")

        case Failure(ex) ⇒
          log.error("Error talking to zmq", ex)
      }

    }

  }
}

