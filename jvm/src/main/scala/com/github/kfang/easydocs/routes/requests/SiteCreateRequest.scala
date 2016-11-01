package com.github.kfang.easydocs.routes.requests

import java.util.UUID
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.ElasticClient
import com.github.kfang.easydocs.models.ESSite
import com.github.kfang.easydocs.routes.responses.SiteCreateResponse
import com.github.kfang.easydocs.utils.ERR
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.concurrent.{Future, ExecutionContext}

case class SiteCreateRequest(
  name: String
)

object SiteCreateRequest {
  implicit val siteCreateJS = jsonFormat1(SiteCreateRequest.apply)

  implicit class SiteCreate(request: SiteCreateRequest)(implicit ec: ExecutionContext, client: ElasticClient){

    private def checkExists: Future[Option[(String, String)]] = {
      ESSite.fromName(request.name).map(_ => {
        Some(ERR.SITE_EXISTS)
      }).recover({
        case e: Exception => None
      })
    }

    private def checkRegex: Future[Option[(String, String)]] = Future.successful {
      val matches = request.name.matches("""[0-9a-zA-Z-]+""")
      if(matches) None else Some(ERR.SITE_REGEX_MISMATCH)
    }

    private def createSite: Future[SiteCreateResponse] = {
      val site = ESSite(
        id = UUID.randomUUID().toString,
        name = request.name
      )
      client.execute(index.into(ESSite.ALIAS_TYPE).source(site.toJson.compactPrint).id(site.id))
        .map(res => SiteCreateResponse(site))
    }

    def getResponse: Future[SiteCreateResponse] = (for {
      exists <- checkExists
      regex <- checkRegex
    } yield { List(exists, regex).flatten }).flatMap({
      case Nil => createSite
      case ers => throw ERR.badRequest(ers)
    })

  }

}

