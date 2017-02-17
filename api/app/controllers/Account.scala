// package controllers
//
// import javax.inject._
// import play.api._
// import play.api.mvc._
// import play.api.libs.json.Json
// import core.services.UserService
//
// import com.mohiva.play.silhouette.api.{ LoginEvent, Silhouette }
// import utils.authentication.DefaultEnv
//
// class Account @Inject() (
//   userService: UserService,
//   silhouette: Silhouette[DefaultEnv],
//   environment: play.api.Environment,
//   configuration: play.api.Configuration) extends Controller {
//
//   // def login = Action {
//   //   silhouette.env.authenticatorService.init(authenticator).map { token =>
// 	// 							Ok(Json.obj("token" -> token))
// 	// 						}
//   // }
//
//   def me = silhouette.SecuredAction { implicit request =>
//     Ok(Json.toJson(request.identity))
//   }
//
// }
