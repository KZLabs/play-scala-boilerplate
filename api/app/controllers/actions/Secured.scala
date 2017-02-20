package controllers

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._

import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json._

import pdi.jwt._
import core.entities.User

case class TokenUser(id: Long) {
  //def isAdmin: Boolean = (name.toLowerCase == "admin")
}

object TokenUser {
  def fromUser(user: User): TokenUser = TokenUser(user.id)
  implicit val userFormat = Json.format[TokenUser]
}

class AuthenticatedRequest[A](val user: TokenUser, request: Request[A]) extends WrappedRequest[A](request)

trait Secured {
  def Authenticated = AuthenticatedAction
  //def Admin = AdminAction
}

object AuthenticatedAction extends ActionBuilder[AuthenticatedRequest] {
  def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]) =
    request.jwtSession.getAs[TokenUser]("user") match {
      case Some(user) => block(new AuthenticatedRequest(user, request)).map(_.refreshJwtSession(request))
      case _ => Future.successful(Unauthorized)
    }
}

// object AdminAction extends ActionBuilder[AuthenticatedRequest] {
//   def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]) =
//     request.jwtSession.getAs[TokenUser]("user") match {
//       case Some(user) if user.isAdmin => block(new AuthenticatedRequest(user, request)).map(_.refreshJwtSession(request))
//       case Some(_) => Future.successful(Forbidden.refreshJwtSession(request))
//       case _ => Future.successful(Unauthorized)
//     }
// }
