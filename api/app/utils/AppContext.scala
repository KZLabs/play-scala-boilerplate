package utils

import com.google.inject._
import io.getquill._
import java.io.Closeable
import javax.sql.DataSource

import core.services._
import core.buckets._
import core.entities._
import storage.production._

class AppContext @Inject() (
    val environment: play.api.Environment,
    val configuration: play.api.Configuration
) {

  private val db = new MysqlJdbcContext[SnakeCase](configuration.underlying.getConfig("database"))

  val bucketContext: BucketContext = BucketContext(new ProdUserBucket(db), new ProdNotificationBucket())

  val userService: UserService = new UserService(bucketContext)
}
