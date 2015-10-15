package easydocs.utils

import spray.http.{StatusCode, StatusCodes}
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import spray.json._
import spray.routing.directives.RouteDirectives

case class ERR(code: StatusCode, errors: Map[String, JsValue]) extends Exception {

  def error: (StatusCode, JsObject) = {
    val body = JsObject(Map("error" -> JsBoolean(x = true), "code" -> JsNumber(code.intValue)) ++ errors)
    (code, JsObject("meta" -> body))
  }

  def complete: spray.routing.Route = RouteDirectives.complete(error)
}

object ERR {

  def apply(code: StatusCode, errors: List[(String, String)]): ERR = ERR(code, errors.map({case (k, v) => k -> JsString(v)}).toMap)

  def apply(code: StatusCode, error: (String, String)): ERR = ERR(code, List(error))

  def apply(errors: List[(String, String)]): ERR = ERR(StatusCodes.InternalServerError, errors)

  def apply(error: (String, String)): ERR = ERR(StatusCodes.InternalServerError, List(error))

  def apply(e: Throwable): ERR = e match {
    case e: ERR => e
    case _ => ERR(StatusCodes.InternalServerError, Map(
      ("exception", JsString(e.getMessage)),
      ("stackTrace", JsArray(e.getStackTrace.map(l => JsString(l.toString)).toVector))
    ))
  }

  def internalServerError(error: (String, String)): ERR = ERR(StatusCodes.InternalServerError, error)
  def badRequest(errors: List[(String, String)]): ERR = ERR(StatusCodes.BadRequest, errors)
  def badRequest(errors: (String, String)*): ERR = badRequest(errors.toList)
  def notFound(errors: List[(String, String)]): ERR = ERR(StatusCodes.NotFound, errors)
  def notFound(errors: (String, String)*): ERR = notFound(errors.toList)


  val TOPIC_SUBTOPIC_EXISTS = "topic-subtopic-exists" -> "topic and subtopic already exists"
  val ROUTE_METHOD_TYPE_EXISTS = "route-method-type-exists" -> "route, method, and type combination already exists"
  val FIELDS_HAVE_BLANK = "fields-have-blank" -> "no fields should have empty string"

  val ENDPOINT_MISSING = "endpoint-missing" -> "endpoint was not found"
  val SITE_REGEX_MISMATCH = "site-regex-mismatch" -> "improper characters found, only alphanumeric and dashes"
  val SITE_EXISTS = "site-exists" -> "site name already exists"
  val SITE_MISSING = "site-missing" -> "site was not found"
}