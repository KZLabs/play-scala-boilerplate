package storage.production

import core.buckets.UserBucket
import core.entities._
import io.getquill._

import scala.concurrent.Future

class ProdUserBucket(val context: db.DbContext) extends UserBucket {
  import context._

  val users = quote(querySchema[User]("users"))

  def findByEmail(email: String): Option[User] = run(users.filter(_.email == lift(email))).headOption

  def save(user: User): Option[User] = Some(user)
}

object ProdUserBucket {

}
