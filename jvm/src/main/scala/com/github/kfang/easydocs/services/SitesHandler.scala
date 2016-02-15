package com.github.kfang.easydocs.services

import akka.actor._
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.analyzers.{StandardAnalyzer, KeywordAnalyzer}
import com.sksamuel.elastic4s.mappings.DynamicMapping
import com.sksamuel.elastic4s.mappings.FieldType.StringType

import scala.concurrent.Future
import scala.util.{Failure, Success}

class SitesHandler(elasticClient: ElasticClient) extends Actor with ActorLogging {
  import context.dispatcher

  private val INDEX = "ezdocs-sites"
  private val MAPPING = mapping("site").fields(
    field("id").typed(StringType).analyzer(KeywordAnalyzer),
    field("name").typed(StringType).analyzer(StandardAnalyzer)
  ).dynamic(DynamicMapping.False)

  private def createIndex(): Future[Boolean] = {
    elasticClient
      .execute(create.index(INDEX).mappings(MAPPING).replicas(1))
      .map(_.isAcknowledged)
  }

  private def ensureIndex(): Future[Boolean] = {
    elasticClient.execute(index.exists(INDEX)).flatMap(_.isExists match {
      case true  => Future.successful(false)
      case false => createIndex()
    }).andThen({
      case Success(b) => log.debug(s"Sites index created: $b")
      case Failure(e) => log.error(e, "Failed to create sites index")
    })
  }

  override def preStart(): Unit = {
    self ! "ENSURE_INDEX"
  }

  def receive = {
    case "ENSURE_INDEX" => ensureIndex()
    case msg => log.debug("unknown message received"); self ! PoisonPill
  }

}

object SitesHandler {
  val NAME = "ez-sites-handler"
  def props(elasticClient: ElasticClient): Props = Props(classOf[SitesHandler], elasticClient)
}

