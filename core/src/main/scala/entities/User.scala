package core.entities

case class User(
  userId: String,
  firstName: String,
  lastName: String,
  fullName: String,
  email: String,
  var password: String)
