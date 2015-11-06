package csw.pkgDemo.hcd2

import akka.actor.{ Props, Actor, ActorLogging }
import akka.util.ByteString
import org.zeromq.ZMQ
import scala.concurrent.Future

object ZmqClient {
  def props(url: String): Props = Props(classOf[ZmqClient], url)

  // Type of a command sent to the ZMQ socket
  case class Command(m: ByteString)
}

/**
 * Using Jeromq, since akka-zeromq not available in Scala-2.11
 */
class ZmqClient(url: String) extends Actor with ActorLogging {
  import context.dispatcher

  val zmqContext = ZMQ.context(1)
  val socket = zmqContext.socket(ZMQ.REQ)
  socket.connect(url)

  override def receive: Receive = {
    case ZmqClient.Command(byteString) ⇒
      val replyTo = sender()
      Future {
        socket.send(byteString.toArray, 0)
        val reply = socket.recv(0)
        replyTo ! ByteString(reply)
      }

    case x ⇒ log.info(s"Unexpected Message from ZMQ: $x")
  }
}
