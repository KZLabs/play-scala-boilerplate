package models

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/*
* Stores the Api Key information
*/
case class ApiKey(
  apiKey: String,
  name: String,
  active: Boolean
)
object ApiKey {
  // API KEYS. TODO: store in db
  val apiKeys = Map[String, ApiKey](
    "2iG2pVmEkZ5jUXORyZtkPXJ3rPUFYgFfTyOh3rhrDQXFfX4VVgNYCz646QuudPf" -> ApiKey(apiKey = "2iG2pVmEkZ5jUXORyZtkPXJ3rPUFYgFfTyOh3rhrDQXFfX4VVgNYCz646QuudPf", name = "web-app", active = true),
    "tmPxrW4j47tgsLb0XjZsjEDnR74rS71jZZfWftf1DVYb6VW84sHYaTyAVixJXY4" -> ApiKey(apiKey = "tmPxrW4j47tgsLb0XjZsjEDnR74rS71jZZfWftf1DVYb6VW84sHYaTyAVixJXY4", name = "android-app", active = false)
  )

  def isActive(key: String): Future[Option[Boolean]] = Future.successful {
    apiKeys.get(key) match {
      case Some(ak) => Some(ak.active)
      case _ => Some(false)
    }

  }

}
