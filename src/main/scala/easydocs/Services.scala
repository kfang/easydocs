package easydocs

import akka.actor.ActorSystem

trait Services {
  implicit val system = ActorSystem("easydoc")
  import system.dispatcher
  val esClient = new Client()

}
