package csw.pkgDemo.hcd2

import akka.actor.{Props, Actor, ActorLogging}
import akka.util.ByteString
import org.zeromq.ZMQ

object ZmqClient {
  def props(url: String): Props = Props(classOf[ZmqClient], url)

  // Message received by the actor to move the wheel by 1 position
  case object Move

  // Message sent to ZMQ
  val zmqMsg: Array[Byte] = ByteString("1", ZMQ.CHARSET.name()).toArray
}

/**
 * Using Jeromq, since akka-zeromq not available in Scala-2.11
 */
class ZmqClient(url: String) extends Actor with ActorLogging {

  import ZmqClient._

  private val zmqContext = ZMQ.context(1)
  private val socket = zmqContext.socket(ZMQ.REQ)
  socket.connect(url)

  override def receive: Receive = {
    case Move ⇒
      socket.send(zmqMsg, 0)
      val reply = socket.recv(0)
      sender() ! ByteString(reply)

    case x ⇒ log.info(s"Unexpected Message from ZMQ: $x")
  }
}
