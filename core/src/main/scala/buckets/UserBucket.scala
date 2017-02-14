package core.buckets

import core.entities.User

trait UserBucket {
  def findByEmail(email: String): Option[User]
}
