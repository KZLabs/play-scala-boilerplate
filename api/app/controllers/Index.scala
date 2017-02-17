package controllers

import javax.inject._

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.duration._
import play.api._
import play.api.mvc._
import play.api.libs.json.{JsError, Json}
import utils._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import scala.concurrent.{Future, Promise}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration._

import scala.util.{Failure, Success}

class Index @Inject() (appContext: AppContext) extends Controller{

  def index = Action.async {

    val login = appContext.userService.login("jhon.doe@mail.com", "password")

    login.map(user => Ok(Json.obj("name" -> user.firstName, "version" -> "1.0", "environment" -> appContext.environment.mode)) )
    .recover {
      case e => Unauthorized (Json.obj ("message" -> e.getMessage, "errors" -> e.getStackTrace.toString) )
    }

  }

}
