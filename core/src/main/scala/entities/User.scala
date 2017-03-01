package core.entities

trait UserBase {
  def id: Long
  def firstName: String
  def lastName: String
  def email: String
  def password: String
  def emailVerified: Boolean
  def emailVerifiedAt: Option[Long]
  def pendingReset: Boolean
  def lastLoginAt: Option[Long]
  def updatedAt: Long
  def createdAt: Long
}

case class User(
  val id: Long,
  val firstName: String,
  val lastName: String,
  val email: String,
  var password: String,
  val emailVerified: Boolean = false,
  val emailVerifiedAt: Option[Long],
  val pendingReset: Boolean = false,
  val lastLoginAt: Option[Long],
  val updatedAt: Long,
  val createdAt: Long
) extends UserBase
