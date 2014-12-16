package easydocs.client

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.sksamuel.elastic4s.{ElasticDsl, ElasticClient}
import com.sksamuel.elastic4s.source.ObjectSource
import easydocs.client.DocumentClient.{EndpointReadRequest, EndpointDeleteRequest, EndpointUpdateRequest, EndpointInsertRequest}
import org.elasticsearch.common.settings.ImmutableSettings
import com.sksamuel.elastic4s.ElasticDsl._
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object DocumentClient {
  def props: Props = Props(classOf[DocumentClient])
  case class EndpointInsertRequest(endpoint: ESEndpoint)
  case class EndpointUpdateRequest(endpoint: ESEndpoint)
  case class EndpointDeleteRequest(endpointID: String)
  case class EndpointReadRequest(endpointID: String)
}

class DocumentClient extends Actor with ActorLogging {

  private val USE_LOCAL = false
  private val ES_HOST = sys.env.getOrElse("ES_HOST", "localhost")

  private val INDEX = "easydocs-v1"
  private val TYPE = "endpoint"
  private val INDEX_TYPE = INDEX -> TYPE

  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  private val client = if(USE_LOCAL) {
    ElasticClient.local
  } else {
    val elasticSearchSettings = ImmutableSettings.settingsBuilder().put("cluster.name", "zc0").build()
    ElasticClient.remote(elasticSearchSettings, (ES_HOST, 9300))
  }

  private def upsert(endpoint: ESEndpoint): Future[Boolean] = Try {
    index.into(INDEX_TYPE).doc(ObjectSource(endpoint)).id(endpoint.id)
  } match {
    case Failure(e)   => log.error(e, "insert exception =>"); Future.successful(false)
    case Success(cmd) =>
      client.execute(cmd).map(ir => {log.info("insert success: " + endpoint.id); true})
        .recover({case e: Exception => log.error(e, "insert exception =>"); false})
  }

  //TODO: handle stuff with sender
  private def insert(request: EndpointInsertRequest, requestor: ActorRef): Future[Boolean] = upsert(request.endpoint)

  //TODO: handle stuff with sender
  private def update(request: EndpointUpdateRequest, requestor: ActorRef): Future[Boolean] = upsert(request.endpoint)

  //TODO: handle stuff with sender
  private def delete(request: EndpointDeleteRequest, requestor: ActorRef): Future[Boolean] = {
    val cmd = ElasticDsl.delete.from(INDEX_TYPE).where(ids(request.endpointID))
    client.execute(cmd).map(_ => true).recover({case e: Exception => log.error(e, "delete exception =>"); false})
  }

  override def receive = {
    case msg: EndpointInsertRequest => insert(msg, sender())
    case msg: EndpointUpdateRequest => update(msg, sender())
    case msg: EndpointDeleteRequest => delete(msg, sender())
    case msg: EndpointReadRequest => //TODO
    case msg => log.debug("unknown message received: " + msg)
  }

}


