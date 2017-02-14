package utils

import com.google.inject._

import core.services._
import core.buckets._
import core.entities._
import storage.production._

class AppContext @Inject() (
  val environment: play.api.Environment,
  val configuration: play.api.Configuration
) {
  val bucketContext: BucketContext =
    new BucketContext(new ProdUserBucket(), new ProdNotificationBucket())

  val userService: UserService = new UserService(bucketContext)
}
