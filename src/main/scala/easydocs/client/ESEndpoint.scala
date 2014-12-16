package easydocs.client

case class ESEndpoint(
  id: String,
  topic: String,
  subTopic: String,
  route: Array[String],
  method: String,
  authentication: String,
  contentType: String,
  description: String,
  parameters: String
)
