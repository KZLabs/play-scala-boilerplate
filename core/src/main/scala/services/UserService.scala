package core.services

import core.entities._
import core.buckets.UserBucket
import com.github.t3hnar.bcrypt._

class UserService (userBucket : UserBucket) {

  def login(email: String, password: String): Either[String, User] = {

    val user = userBucket.findByEmail(email)

    if (user.isDefined && password.isBcrypted(user.get.password))
      Right(user.get)
    else
      Left("Invalid user or password")
  }

}

object UserService {

}
