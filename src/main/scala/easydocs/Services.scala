package easydocs

import akka.actor.ActorSystem

trait Services {

  implicit val system = ActorSystem("easydoc")
  import system.dispatcher

  implicit val esClient = new Client()

}
