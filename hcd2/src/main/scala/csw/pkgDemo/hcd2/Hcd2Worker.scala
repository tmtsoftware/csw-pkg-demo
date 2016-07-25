package csw.pkgDemo.hcd2

import akka.actor._
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import csw.services.log.PrefixedActorLogging
import csw.util.config.Configurations._
import org.zeromq.ZMQ
import csw.util.config.ConfigDSL._

import scala.language.postfixOps

object Hcd2Worker {
  def props(prefix: String): Props = Props(classOf[Hcd2Worker], prefix)

  val settings = ConfigFactory.load("zmq")

  // Message requesting current state of HCD values
  case object RequestCurrentState

}

/**
 * An actor that does the work of matching a configuration
 */
class Hcd2Worker(override val prefix: String) extends Actor with PrefixedActorLogging {

  import Hcd2._
  import Hcd2Worker._

  // The key used to talk to ZML
  val zmqKey = prefix.split('.').last

  // The key and list of choices used in configurations and CurrentState objects
  val (key, choices) = if (zmqKey == "filter")
    (filterKey, FILTERS)
  else (disperserKey, DISPERSERS)

  // Get the ZMQ client
  val url = settings.getString(s"zmq.$zmqKey.url")
  log.info(s"For $zmqKey: using ZMQ URL = $url")
  val zmqClient = context.actorOf(ZmqClient.props(url))

  // for the demo just assume positions start at 0, 0
  context.become(working(0, 0))

  override def receive: Receive = Actor.emptyBehavior

  /**
   * Actor state while talking to the ZMQ process
   *
   * @param currentPos The current position (index in filter or disperser array)
   * @param demandPos  The demand (requested) position (index in filter or disperser array)
   */
  def working(currentPos: Int, demandPos: Int): Receive = {
    // Received a SetupConfig (from the assembly): extract the value and send the new position to ZMQ
    case setupConfig: SetupConfig =>
      setupConfig(key).values.foreach { value =>
        val pos = choices.indexOf(value)
        setPos(currentPos, pos)
      }

    // The reply from ZMQ should be the index of the current filter or disperser
    case reply: ByteString =>
      val pos = reply.decodeString(ZMQ.CHARSET.name()).toInt
      log.info(s"ZMQ current pos: $pos")
      val value = choices(pos)
      context.parent ! cs(prefix, key -> value)
      setPos(pos, demandPos)

    // Send the parent the current state
    case RequestCurrentState =>
      context.parent ! cs(prefix, key -> choices(currentPos))

    case x => log.error(s"Unexpected message $x")
  }

  // If the demand pos is not equal to the current pos, increment the position
  // (to simulate the filter or disperser wheel turning one position at a time
  // while updating the telemetry)
  private def setPos(currentPos: Int, demandPos: Int): Unit = {
    context.become(working(currentPos, demandPos))
    if (demandPos != currentPos) {
      zmqClient ! ZmqClient.Move
    }
  }

}

