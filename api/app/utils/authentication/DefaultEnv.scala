package utils.authentication

import play.api.libs.json.{ Format, Json, OFormat }
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.api.{ LoginInfo, Identity, Env }

case class User(
  userID: Long,
  loginInfo: LoginInfo,
  name: String,
  email: Option[String]) extends Identity

object User {
  implicit val userJsonFormat: Format[User] = Json.format[User]
}

/** The default Silhouette Environment.
 */
trait DefaultEnv extends Env {

	/** Identity
	 */
	type I = User

	/** Authenticator used for identification.
	 *  [[BearerTokenAuthenticator]] could've also been used for REST.
	 */
	type A = JWTAuthenticator

}
