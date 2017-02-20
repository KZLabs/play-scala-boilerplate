package controllers

import javax.inject._
import scala.language.postfixOps

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import pdi.jwt._
import utils._
import models._

class Account @Inject() (appContext: AppContext) extends Controller with Secured {

  private val loginData: Reads[(String, String)] =
      (JsPath \ "username").read[String] and
      (JsPath \ "password").read[String] tupled

  def login = Action(parse.json) { implicit request =>
    request.body.validate(loginData).fold(
      errors => BadRequest(JsError.toJson(errors)),
      data => {
        val (username, password) = data
        val login = appContext.userService.login(username, password)

        login.fold(
          errors => {
            Unauthorized(Json.obj("message" -> errors, "errors" -> errors))
          },
          data => {
            var result = Ok.addingToJwtSession("user", TokenUser.fromUser(data))

            val token = result.jwtSession.serialize
            val loginUser = LoginUser.fromUser(token, data)

            Ok(Json.obj("user" -> Json.toJson(loginUser) ))
          }
        )
      }
    )
  }

  def me = Authenticated { implicit request =>

    Ok(s"Only the best can see that. ${request.user.id}")
  }
}
