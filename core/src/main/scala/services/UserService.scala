package core.services

import java.security.InvalidParameterException

import core.entities._
import core.buckets._
import com.github.t3hnar.bcrypt._

import scala.concurrent.{ Future, Promise }
import scala.util.{ Success, Failure }
import scala.concurrent.ExecutionContext.Implicits.global

class UserService(context: BucketContext) {

  val userBucket: UserBucket = context.userBucket
  val notificationBucket: NotificationBucket = context.notificationBucket

  def login(email: String, password: String): Either[String, User] = {

    val user = userBucket.findByEmail(email)

    if (!user.isEmpty && password.isBcrypted(user.get.password)) Right(user.get)
    else Left("Invalid user or password")
  }

  /*  def signup(newUser: User): Either[String, User] = {

    def sendVerificationEmail(user: User) = {
      val token: String = "" //TODO: generate token
      notificationBucket.sendVerificationEmail(user, token)
    }
    //TODO: validate newUser

    var user = userBucket.findByEmail(newUser.email)

    user.onSuccess()

    if (user.isDefined )
      Left("The user already exists")
    else {
      newUser.password = newUser.password.bcrypt
      user = userBucket.save(newUser)
      if (user.isDefined && sendVerificationEmail(user.get))
        Right(user.get)
      else
        Future.failed(new Exception("The user could not be created"))
    }
    Future.failed(new Exception("Invalid user or password"))
  }*/

}

object UserService {

}
