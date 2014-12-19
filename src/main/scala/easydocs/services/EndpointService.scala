package easydocs.services

import akka.actor.{Props, Actor, ActorLogging}
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import easydocs.models.ESEndpoint
import easydocs.services.EndpointService.{TriggerEndpointIndexDelete, TriggerEndpointIndexCreate}
import scala.concurrent.Future

object EndpointService {
  def props(client: ElasticClient): Props = Props(classOf[EndpointService], client)

  case object TriggerEndpointIndexCreate
  case object TriggerEndpointIndexDelete

}

class EndpointService(client: ElasticClient) extends Actor with ActorLogging {

  import context.dispatcher

  private def createEndpointIndex: Future[Boolean] = (for {
    indexCreate <- client.execute(create.index(ESEndpoint.INDEX).mappings(ESEndpoint.MAPPING).replicas(0).shards(6))
    aliasCreate <- client.execute(aliases.add(ESEndpoint.ALIAS).on(ESEndpoint.INDEX))
  } yield {
    log.info("index creation: " + indexCreate.isAcknowledged)
    log.info("alias creation: " + aliasCreate.isAcknowledged)
    true
  }).recover({
    case e: Exception => log.error(e, "createEndpointIndex exception =>"); false
  })

  private def deleteEndpointIndex: Future[Boolean] = {
    client.execute(delete.index(ESEndpoint.INDEX)).map(indexDelete => {
      log.info("index deletion: " + indexDelete.isAcknowledged)
      true
    }).recover({
      case e: Exception => log.error(e, "deleteEndpointIndex exception =>"); false
    })
  }

  override def preStart = {
    //print out stats if the index exists, send creation if it doesn't
    client.execute(status(ESEndpoint.INDEX)).map(indexStatuses => {
      val indexStatus = indexStatuses.getIndex(ESEndpoint.INDEX)
      val numDocs = indexStatus.getDocs.getNumDocs
      val numBytes = indexStatus.getStoreSize.bytes()
      log.info(s"Index Status => bytes: $numBytes\t docs: $numDocs")
    }).recover({
      case e: Exception => self ! TriggerEndpointIndexCreate
    })
  }

  override def receive = {
    case TriggerEndpointIndexCreate => createEndpointIndex
    case TriggerEndpointIndexDelete => deleteEndpointIndex
    case msg => log.debug("unknown message received " + msg)
  }

}


