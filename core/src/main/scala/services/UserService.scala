package core.services

import core.entities._
import core.buckets._
import com.github.t3hnar.bcrypt._

class UserService (context: BucketContext) {

  val userBucket: UserBucket = context.userBucket
  val notificationBucket: NotificationBucket = context.notificationBucket

  def login(email: String, password: String): Either[String, User] = {

    val user = userBucket.findByEmail(email)

    if (user.isDefined && password.isBcrypted(user.get.password))
      Right(user.get)
    else
      Left("Invalid user or password")
  }

  def signup(newUser: User): Either[String, User] = {

    def sendVerificationEmail(user: User) = {
      val token: String = "" //TODO: generate token
      notificationBucket.sendVerificationEmail(user, token)
    }
    //TODO: validate newUser

    var user = userBucket.findByEmail(newUser.email)

    if (user.isDefined )
      Left("The user already exists")
    else {
      newUser.password = newUser.password.bcrypt
      user = userBucket.save(newUser)
      if (user.isDefined && sendVerificationEmail(user.get))
        Right(user.get)
      else
        Left("The user could not be created")
    }
      Left("Invalid user or password")
  }

}

object UserService {

}
