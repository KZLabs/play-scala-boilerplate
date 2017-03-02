package controllers

import javax.inject._
import scala.language.postfixOps

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.i18n.MessagesApi
import api.ApiError._
import play.api.Logger

import pdi.jwt._
import utils._
import models._
import api._, ApiDSL._

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.language.implicitConversions

class Account @Inject() (appContext: AppContext, val messagesApi: MessagesApi) extends api.ApiController {

  implicit val loginInfoReads: Reads[Tuple2[String, String]] = (
    (__ \ "email").read[String](Reads.email) and
      (__ \ "password").read[String] tupled
  )

  def login = ApiActionWithBody { implicit request =>

    readFromRequest[Tuple2[String, String]] {
      case (email, password) => {
        for {
          user <- appContext.userService.login(email, password) ?| (f => errorUserNotFound)
        } yield {
          val token = ApiToken.create(request.apiKeyOpt.get, user.id)
          val loginUser = LoginUser.fromUser(token, user)
          ok(Json.obj("user" -> Json.toJson(loginUser)))
        }

      }
    }
  }
  def me = SecuredApiAction { implicit request =>
    //maybeItem(User.findById(request.userId))
    ok(Json.obj("user" -> Json.toJson(request.userId)))
  }

  // def login = Action(parse.json) { implicit request =>
  //   request.body.validate(loginData).fold(
  //     errors => BadRequest(JsError.toJson(errors)),
  //     data => {
  //       val (username, password) = data
  //       val login = appContext.userService.login(username, password)
  //
  //       login.fold(
  //         errors => {
  //           Unauthorized(Json.obj("message" -> errors, "errors" -> errors))
  //         },
  //         data => {
  //           //var result = Ok.addingToJwtSession("user", TokenUser.fromUser(data))
  //
  //           ApiToken.create(request.apiKeyOpt.get, user.id)
  //           val token = "sdafads" //result.jwtSession.serialize
  //           val loginUser = LoginUser.fromUser(token, data)
  //
  //           Ok(Json.obj("user" -> Json.toJson(loginUser)))
  //         }
  //       )
  //     }
  //   )
  // }

  // def me = Action { implicit request =>
  //   BadRequest
  //   //Ok(s"Only the best can see that. ${request.user.id}")
  // }
}
