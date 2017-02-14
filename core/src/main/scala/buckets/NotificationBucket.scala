package core.buckets

import core.entities.User

trait NotificationBucket {
  def sendVerificationEmail(user: User, token: String): Boolean
}
