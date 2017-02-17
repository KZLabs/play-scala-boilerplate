package utils

import com.google.inject._
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import core.services._
import core.buckets._
import core.entities._
import storage.production._

class AppContext @Inject() (
  val environment: play.api.Environment,
  val configuration: play.api.Configuration
) {

  private val db = DatabaseConfig.forConfig[JdbcProfile]("database")

  val bucketContext: BucketContext = BucketContext(new ProdUserBucket(db), new ProdNotificationBucket())

  val userService: UserService = new UserService(bucketContext)
}
