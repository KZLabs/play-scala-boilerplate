package core.entities

case class User(
  id: BigInt,
  firstName: String,
  lastName: String,
  email: String,
  var password: String,
  emailVerified: Boolean = false,
  emailVerifiedAt: Option[Long],
  pendingReset: Boolean = false,
  lastLoginAt: Option[Long],
  updatedAt: Long,
  createdAt: Long)
