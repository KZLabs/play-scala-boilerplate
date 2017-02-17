// package filters
//
// import javax.inject._
// import com.mohiva.play.silhouette.api.{ Silhouette }
// import utils.authentication.{ DefaultEnv }
//
// import play.api.mvc._
// import scala.concurrent.{ExecutionContext, Future}
//
// // class SecuredFilter @Inject() (silhouette: Silhouette[DefaultEnv]) extends Filter {
// //
// //   override def apply(next: RequestHeader => Future[Result])(
// //     request: RequestHeader): Future[Result] = {
// //
// //     // val action = silhouette.UserAwareAction.async { r =>
// //     //   request.path match {
// //     //     case "/" => next(request)
// //     //     case _ => if r.identity.isEmpty => Future.successful(Unauthorized)
// //     //   }
// //     // }
// //
// //     //action(request).run
// //   }
// // }
