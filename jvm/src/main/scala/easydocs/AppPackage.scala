package easydocs

import akka.actor.ActorSystem

case class AppPackage(
  system: ActorSystem,
  config: AppConfig,
  services: AppServices
)
