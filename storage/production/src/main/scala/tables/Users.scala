package storage.production.tables

import core.entities.User
import slick.driver.MySQLDriver.api._
import slick.profile.SqlProfile.ColumnOption.SqlType
//import java.time.Instant
//Instant.now.getEpochSecond

class Users(tag: Tag) extends Table[User](tag, "users") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def firstName = column[String]("first_name", O.SqlType("varchar(45)"))
  def lastName = column[String]("last_name", O.SqlType("varchar(45)"))
  def email = column[String]("email", O.SqlType("varchar(255)"))
  def password = column[String]("password", O.SqlType("varchar(255)"))

  def emailVerified = column[Boolean]("email_verified", O.Default(false))
  def emailVerifiedAt = column[Option[Long]]("email_verified_at")

  def pendingReset = column[Boolean]("pending_reset", O.Default(false))
  def lastLoginAt = column[Option[Long]]("last_login_at")

  // this feature is particular to MySQL so it may not work
  // in other rdbms backends
  def updatedAt = column[Long]("updated_at", O.Default(0))
  def createdAt = column[Long]("created_at", O.Default(0))

  // Indexes
  def emailIndex = index("user_email_idx", email, true)

  // Select
  def * = (id, firstName, lastName, email, password, emailVerified, emailVerifiedAt, pendingReset, lastLoginAt, updatedAt, createdAt)  <> (User.tupled, User.unapply)
}

trait UsersTable {
  val users = TableQuery[Users]
}
