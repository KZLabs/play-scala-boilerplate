package core.buckets

import core.entities.User
import scala.concurrent.Future

trait UserBucket {
  def findByEmail(email: String): Option[User]
  def save(user: User): Option[User]
}
