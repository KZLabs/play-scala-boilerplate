package models

import play.api.libs.json._
import core.entities._

case class LoginUser(
  val token: String,
  val id: Long,
  val firstName: String,
  val lastName: String,
  val email: String,
  val lastLoginAt: Option[Long],
  val updatedAt: Long,
  val createdAt: Long) extends UserBase {
    def password: String = ""
    def emailVerified: Boolean = true
    def emailVerifiedAt: Option[Long] = None
    def pendingReset: Boolean = false
}

object LoginUser {
  def fromUser(token: String, user: User): LoginUser = {
    import user._
    LoginUser(token, id, firstName, lastName, email, lastLoginAt, updatedAt, createdAt)
  }

  implicit val loginUserWrites = new Writes[LoginUser] {
    def writes(u: LoginUser) = Json.obj(
      "token"-> u.token,
      "id"-> u.id,
      "firstName"-> u.firstName,
      "lastName"-> u.lastName,
      "email"-> u.email,
      "lastLogin"-> u.lastLoginAt,
      "updatedAt"-> u.updatedAt,
      "createdAt"-> u.createdAt
    )
  }
}
