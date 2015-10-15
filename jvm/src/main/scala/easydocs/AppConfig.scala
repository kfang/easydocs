package easydocs

import com.typesafe.config.ConfigFactory

case class AppConfig(
  ENVIRONMENT: String = sys.env.getOrElse("ENVIRONMENT", "local")
) {

  val CONFIG = ConfigFactory.load.getConfig(ENVIRONMENT)
  val IS_PRODUCTION = ENVIRONMENT == "production"

  lazy val ELASTIC_REMOTE_HOST = CONFIG.getString("elastic.remote.host")
  lazy val ELASTIC_REMOTE_PORT = CONFIG.getInt("elastic.remote.port")

}
