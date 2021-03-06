package com.github.kfang.easydocs.services

import akka.actor.{Props, Actor, ActorLogging}
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import com.github.kfang.easydocs.models.{ESSite, ESEndpoint}
import com.github.kfang.easydocs.services.IndexService.{TriggerSiteIndexDelete, TriggerSiteIndexCreate, TriggerEndpointIndexDelete, TriggerEndpointIndexCreate}
import scala.concurrent.Future

object IndexService {

  val NAME = "index-service"

  def props(client: ElasticClient): Props = Props(classOf[IndexService], client)

  case object TriggerEndpointIndexCreate
  case object TriggerEndpointIndexDelete
  case object TriggerSiteIndexCreate
  case object TriggerSiteIndexDelete

}

class IndexService(client: ElasticClient) extends Actor with ActorLogging {

  import context.dispatcher

  /**********************************************************************************
   * Endpoint Index Operations
   *********************************************************************************/
  private def createEndpointIndex: Future[Boolean] = (for {
    indexCreate <- client.execute(create.index(ESEndpoint.INDEX).mappings(ESEndpoint.MAPPING).replicas(0).shards(6))
    aliasCreate <- client.execute(addAlias(ESEndpoint.ALIAS).on(ESEndpoint.INDEX))
  } yield {
    log.info("endpoint index creation: " + indexCreate.isAcknowledged)
    log.info("endpoint alias creation: " + aliasCreate.isAcknowledged)
    true
  }).recover({
    case e: Exception => log.error(e, "createEndpointIndex exception =>"); false
  })

  private def deleteEndpointIndex: Future[Boolean] = {
    client.execute(delete.index(ESEndpoint.INDEX)).map(indexDelete => {
      log.info("endpoint index deletion: " + indexDelete.isAcknowledged)
      true
    }).recover({
      case e: Exception => log.error(e, "deleteEndpointIndex exception =>"); false
    })
  }

  /**********************************************************************************
    * Site Index Operations
    *********************************************************************************/
  private def createSiteIndex: Future[Boolean] = (for {
    indexCreate <- client.execute(create.index(ESSite.INDEX).mappings(ESSite.MAPPING).replicas(0).shards(6))
    aliasCreate <- client.execute(addAlias(ESSite.ALIAS).on(ESSite.INDEX))
  } yield {
    log.info("site index creation: " + indexCreate.isAcknowledged)
    log.info("site alias creation: " + aliasCreate.isAcknowledged)
    true
  }).recover({
    case e: Exception => log.error(e, "createSiteIndex exception =>"); false
  })

  private def deleteSiteIndex: Future[Boolean] = {
    client.execute(delete.index(ESSite.INDEX)).map(indexDelete => {
      log.info("site index deletion: " + indexDelete.isAcknowledged)
      true
    }).recover({
      case e: Exception => log.error(e, "deleteSiteIndex exception =>"); false
    })
  }

  /**********************************************************************************
    * Service Operations
    *********************************************************************************/
  override def preStart = {
    //print out stats if the index exists, send creation if it doesn't
    client.execute(index.stats(ESEndpoint.INDEX)).map(is => {
      val stats = is.indexStats(ESEndpoint.INDEX).getPrimaries
      val docStats = stats.docs
      val numDocs = docStats.getCount
      val numBytes = stats.store.getSizeInBytes
      log.info(s"Endpoint Index Status => bytes: $numBytes\t docs: $numDocs")
    }).recover({
      case e: Exception => self ! TriggerEndpointIndexCreate
    })

    client.execute(index.stats(ESSite.INDEX)).map(is => {
      val stats = is.indexStats(ESEndpoint.INDEX).getPrimaries
      val docStats = stats.docs
      val numDocs = docStats.getCount
      val numBytes = stats.store.getSizeInBytes
      log.info(s"Site Index Status => bytes: $numBytes\t docs: $numDocs")
    }).recover({
      case e: Exception => self ! TriggerSiteIndexCreate
    })

  }

  override def receive = {
    case TriggerEndpointIndexCreate => createEndpointIndex
    case TriggerEndpointIndexDelete => deleteEndpointIndex
    case TriggerSiteIndexCreate => createSiteIndex
    case TriggerSiteIndexDelete => deleteSiteIndex
    case msg => log.debug("unknown message received " + msg)
  }

}


