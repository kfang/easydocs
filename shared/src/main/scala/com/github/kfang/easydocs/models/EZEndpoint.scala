package com.github.kfang.easydocs.models

case class EZEndpoint(
  id: String,
  site: String,

  topic: String,
  subTopic: String,
  notes: Option[String],

  route: String,
  method: String,
  contentType: String,

  authentication: Option[String],
  parameters: Option[String]
)
