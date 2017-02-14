package utils

import com.google.inject._

import core.services._
import core.buckets._
import core.entities._
import storage.production._

class AppContext @Inject() (
  environment: play.api.Environment,
  configuration: play.api.Configuration
) {
  val userService : UserService = new UserService(new ProdUserBucket())
}
