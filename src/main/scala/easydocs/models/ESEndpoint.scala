package easydocs.models

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{StandardAnalyzer, KeywordAnalyzer}
import com.sksamuel.elastic4s.mappings.FieldType.{MultiFieldType, StringType}

case class ESEndpoint(
  id: String,

  topic: String,
  subTopic: String,
  notes: Option[String],

  route: String,
  method: String,
  contentType: String,

  authentication: Option[String],
  parameters: Option[String]
)

object ESEndpoint {

  val ALIAS = "easydocs"
  val INDEX = "easydocs-v1"
  val TYPE  = "endpoint"

  val ALIAS_TYPE = ALIAS -> TYPE

  val MAPPING = TYPE.as(
    "id".typed(StringType).analyzer(KeywordAnalyzer),

    "topic".typed(MultiFieldType).as(
      "topic".typed(StringType).analyzer(KeywordAnalyzer),
      "analyzed".typed(StringType).analyzer(StandardAnalyzer)
    ),
    "subTopic".typed(MultiFieldType).as(
      "subTopic".typed(StringType).analyzer(KeywordAnalyzer),
      "analyzed".typed(StringType).analyzer(StandardAnalyzer)
    ),
    "notes".typed(StringType).analyzer(StandardAnalyzer),

    "route".typed(StringType).analyzer(KeywordAnalyzer),
    "method".typed(StringType).analyzer(KeywordAnalyzer),
    "contentType".typed(StringType).analyzer(KeywordAnalyzer),

    "authentication".typed(StringType).analyzer(KeywordAnalyzer),
    "parameters".typed(StringType).analyzer(StandardAnalyzer)
  )

}

