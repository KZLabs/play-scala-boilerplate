package storage.production.tables

import core.entities.User
import slick.profile.SqlProfile.ColumnOption.SqlType

trait UsersTable { this: Db =>
  import config.driver.api._

  private class Users(tag: Tag) extends Table[User](tag, "users") {

    def id = column[BigInt]("id", O.PrimaryKey, O.AutoInc)

    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def email = column[String]("email")
    def password = column[String]("password")

    def emailVerified = column[String]("email_verified")
    def emailVerifiedAt = column[Option[Long]]("email_verified_at")

    def pendingReset = column[String]("pending_reset")
    def lastLoginAt = column[Option[Long]]("last_login_at")

    // this feature is particular to MySQL so it may not work
    // in other rdbms backends
    def updatedAt = column[Long]("updated", SqlType("timestamp not null default UNIX_TIMESTAMP on update UNIX_TIMESTAMP"))
    def createdAt = column[Long]("created", SqlType("timestamp not null default UNIX_TIMESTAMP on update UNIX_TIMESTAMP"))

    // Indexes
    def emailIndex = index("user_email_idx", email, true)

    // Select
    def * = (id, firstName, lastName, email, password, emailVerified, emailVerifiedAt, pendingReset, lastLoginAt, updatedAt, createdAt) <>(User.tupled, User.unapply)
  }

  val users = TableQuery[Users]
}
