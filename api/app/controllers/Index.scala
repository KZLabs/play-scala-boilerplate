package controllers

import javax.inject._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration._
import play.api._
import play.api.mvc._
import play.api.libs.json.{ JsError, Json }
import utils._

import play.api.i18n.{ I18nSupport, Messages, MessagesApi }

class Index @Inject() (appContext: AppContext) extends Controller{

  def index = Action  {
    appContext.userService.login("jhon", "password").fold(errors => {
				Unauthorized(Json.obj("message" -> errors, "errors" -> errors))
			},
			data => {
        Ok(Json.obj("name" -> data.fullName, "version" -> "1.0", "environment" -> environment.mode))
      })

  }

}
