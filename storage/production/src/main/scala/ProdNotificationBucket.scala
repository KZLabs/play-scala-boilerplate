package storage.production

import core.buckets.NotificationBucket
import core.entities._

class ProdNotificationBucket extends NotificationBucket {
  def sendVerificationEmail(user: User, token: String): Boolean = true
}

object ProdNotificationBucket {

}
