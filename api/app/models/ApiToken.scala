package models

import org.joda.time.DateTime
import java.util.UUID
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play
import pdi.jwt._
import pdi.jwt.algorithms.JwtHmacAlgorithm
import play.api.libs.json.Json
import com.google.inject._
import play.Configuration;

/*
* Stores the Auth Token information. Each token belongs to a Api Key and user
*/
case class ApiToken(
    token: String,
    apiKey: String,
    expirationTime: DateTime,
    userId: Long
) {
  def isExpired = expirationTime.isBeforeNow
}

object ApiToken {

  val configuration = Configuration.root()

  def wrap[T](getter: String => T): String => Option[T] = key => try {
    val value = getter(key)
    if (value == None || value == null) None
    else Some(value)
  } catch {
    case e: com.typesafe.config.ConfigException.Null => None
    case e: java.lang.RuntimeException => {
      e.getCause() match {
        case _: com.typesafe.config.ConfigException.Null => None
        case _ => throw e
      }
    }
  }

  val getConfigString = wrap[String](
    key => configuration.getString(key)
  )

  val getConfigMillis = wrap[Long](
    key => configuration.getMilliseconds(key)
  )

  val HEADER_NAME: String = getConfigString("play.http.session.jwtName").getOrElse("Authorization")

  val MAX_AGE: Option[Long] = getConfigMillis("play.http.session.maxAge").map(_ / 1000)

  val ALGORITHM: JwtHmacAlgorithm =
    getConfigString("play.http.session.algorithm")
      .map(JwtAlgorithm.fromString)
      .flatMap {
        case algo: JwtHmacAlgorithm => Option(algo)
        case _ => throw new RuntimeException("You can only use HMAC algorithms for [play.http.session.algorithm]")
      }
      .getOrElse(JwtAlgorithm.HS256)

  val TOKEN_PREFIX: String = getConfigString("play.http.session.tokenPrefix").getOrElse("Bearer ")

  private def key: Option[String] = getConfigString("play.crypto.secret")

  def defaultHeader: JwtHeader = key.map(_ => JwtHeader(ALGORITHM)).getOrElse(JwtHeader())

  def createClaim(apiKey: String, userId: Long): JwtClaim = MAX_AGE match {
    case Some(seconds) => JwtClaim(Json.obj(("userId", userId)).toString, Some(apiKey)).expiresIn(seconds)
    case _ => JwtClaim(Json.obj(("userId", userId)).toString, Some(apiKey))
  }

  private def deserialize(token: String, options: JwtOptions): Option[ApiToken] = (key match {
    case Some(k) => JwtJson.decodeJson(token, k, Seq(ALGORITHM), options)
    case _ => JwtJson.decodeJson(token, options)
  }).map { data =>
    val userId = (data \ "userId").as[Long]
    val issuer = (data \ "iss").as[String]
    val exp = (data \ "exp").as[Long]

    Some(ApiToken(token, issuer, new DateTime(exp * 1000), userId))
  }.getOrElse(None)

  private def deserialize(token: String): Option[ApiToken] = deserialize(token, JwtOptions.DEFAULT)

  def findByTokenAndApiKey(token: String, apiKey: String): Future[Option[ApiToken]] = Future.successful {
    deserialize(token) match {
      case Some(apiToken) if apiToken.apiKey == apiKey => Some(apiToken)
      case _ => None
    }
  }

  def create(apiKey: String, userId: Long): String = key match {
    case Some(k) => JwtJson.encode(defaultHeader, createClaim(apiKey, userId), k)
    case _ => JwtJson.encode(defaultHeader, createClaim(apiKey, userId))
  }

}
