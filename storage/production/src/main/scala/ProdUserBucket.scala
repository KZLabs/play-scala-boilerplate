package storage.production

import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import core.buckets.UserBucket
import core.entities._
import storage.production.tables.UsersTable
import slick.driver.MySQLDriver.api._
import scala.concurrent.Future

class ProdUserBucket(val config: DatabaseConfig[JdbcProfile]) extends UserBucket with UsersTable  {

  val db = config.db

  def findByEmail(email: String): Future[User] = db.run(users.filter(_.email === email).result.head)

  def save(user: User): Future[User] = Future.successful[User](user)
}

object ProdUserBucket {

}
