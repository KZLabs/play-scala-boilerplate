package core.buckets

import core.entities.User
import scala.concurrent.Future

trait UserBucket {
  def findByEmail(email: String): Future[User]
  def save(user: User): Future[User]
}
