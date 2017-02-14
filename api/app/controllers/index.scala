package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import core._


class Index @Inject() (environment: play.api.Environment,
    configuration: play.api.Configuration)
  extends Controller {

  def index = Action {

    Ok(Json.obj("version" -> "1.0", "environment" -> environment.mode))
  }

}
