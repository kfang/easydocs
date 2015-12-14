package com.github.kfang.easydocs.services

import akka.actor._
import com.github.kfang.easydocs.models.EZSite
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._

import scala.concurrent.Future
import scala.util.Success

class SitesHandler(elasticClient: ElasticClient) extends Actor with ActorLogging {
  import context.dispatcher

  private def createIndex(): Future[Boolean] = {
    elasticClient
      .execute(create.index(EZSite.INDEX).mappings().analysis().replicas(1))
      .map(_.isAcknowledged)
  }

  private def ensureIndex(): Future[Boolean] = {
    elasticClient.exists(EZSite.INDEX).flatMap(_.isExists match {
      case true  => Future.successful(false)
      case false => createIndex()
    }).andThen({
      case Success(b) => log.debug(s"Sites index created: $b")
    }).andThen({
      case _ => self ! PoisonPill
    })
  }

  def receive = {
    case "ENSURE_INDEX" => ensureIndex()
    case msg => log.debug("unknown message received"); self ! PoisonPill
  }

}

object SitesHandler {
  private def props(elasticClient: ElasticClient): Props = Props(classOf[SitesHandler], elasticClient)

  def ensureSitesIndex(elasticClient: ElasticClient)(implicit actorRefFactory: ActorRefFactory): Unit = {
    actorRefFactory.actorOf(props(elasticClient)) ! "ENSURE_INDEX"
  }
}
