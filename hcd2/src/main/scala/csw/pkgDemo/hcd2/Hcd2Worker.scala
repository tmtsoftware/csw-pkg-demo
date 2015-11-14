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
}

/**
 * An actor that does the work of matching a configuration
 */
class Hcd2Worker(prefix: String) extends Actor with ActorLogging {
  import Hcd2Worker._
  import context.dispatcher
  val zmqKey = prefix.split('.').last
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
   * (XXX Fix this so the ZMQ C code saves the value)
   */
  def submit(setupConfig: SetupConfig): Unit = {
    log.info("Sending message to ZMQ hardware simulation")

    // Note: We could just send the JSON and let the C code parse it, but for now, keep it simple
    // and extract the value here
    val key = if (zmqKey == "filter") StandardKeys.filter else StandardKeys.disperser
    setupConfig.get(key).foreach { value ⇒
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

