package csw.pkgDemo.container2

import akka.actor.Actor

// Dummy class for testing (XXX Needs update for Scala-2.11)
object HwClient {
//  class Listener extends Actor {
//    def receive: Receive = {
//      case Connecting    ⇒ println("ZMQ Connecting")
//
//      case m: ZMQMessage ⇒
//        println(s"ZMQ Message: ${m.frame(0).utf8String}")
//        sendMessage()
//
//      case x  ⇒ println(s"ZMQ Unknown Message: $x")
//    }
//  }
//
//  var count = 0
//
//  val system = ActorSystem("test")
//
//  val listener = system.actorOf(Props(classOf[Listener]))
//
//  val clientSocket = ZeroMQExtension(system).newSocket(
//    SocketType.Req,
//    Listener(listener),
//    Connect("tcp://localhost:6565")
//  )
//
//
//  def sendMessage(): Unit = {
//    println("sending dummy message")
//    clientSocket ! ZMQMessage(ByteString(s"Dummy Message $count from Akka"))
//  }
//
//  def main(args: Array[String]): Unit = {
//    sendMessage()
//  }
}


