package api

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.control.NonFatal
import scala.util.{ Failure, Success, Try }
import scalaz.syntax.either._
import scalaz.syntax.std.option._
import scalaz._

import scala.language.implicitConversions
import play.api.i18n.{ DefaultMessagesApi, I18nSupport, MessagesApi }
import api._
import ApiError._

import core.entities.Fail

/**
 * Inspiration :
 *  - http://fr.slideshare.net/normation/nrm-scala-iocorrectlymanagingerrorsinscalav13
 *  - https://github.com/Kanaka-io/play-monadic-actions
 */
object ApiDSL extends I18nSupport {

  @javax.inject.Inject()
  val messagesApi: MessagesApi = null

  type Step[A] = EitherT[Future, ApiError, A]

  private[ApiDSL] def fromFuture[A](onFailure: Throwable => ApiError)(future: Future[A])(implicit ec: ExecutionContext): Step[A] = {
    EitherT[Future, ApiError, A](
      future.map(_.right).recover {
        case NonFatal(t) => onFailure(t).left
      }
    )
  }

  private[ApiDSL] def fromFOption[A](onNone: => ApiError)(fOption: Future[Option[A]])(implicit ec: ExecutionContext): Step[A] =
    EitherT[Future, ApiError, A](
      fOption.map(_ \/> onNone).recover {
        case NonFatal(t) => errorInternal.left
      }
    )

  private[ApiDSL] def fromFEither[A, B](onLeft: B => ApiError)(fEither: Future[Either[B, A]])(implicit ec: ExecutionContext): Step[A] = {
    EitherT[Future, ApiError, A](fEither.map(_.fold(onLeft andThen \/.left, \/.right)))
  }

  private[ApiDSL] def fromFDisjunction[A, B](onLeft: B => ApiError)(fDisjunction: Future[B \/ A])(implicit ec: ExecutionContext): Step[A] =
    EitherT[Future, ApiError, A](fDisjunction.map(_.leftMap(onLeft)))

  private[ApiDSL] def fromOption[A](onNone: => ApiError)(option: Option[A]): Step[A] =
    EitherT[Future, ApiError, A](Future.successful(option \/> onNone))

  private[ApiDSL] def fromDisjunction[A, B](onLeft: B => ApiError)(disjunction: B \/ A)(implicit ec: ExecutionContext): Step[A] =
    EitherT[Future, ApiError, A](Future.successful(disjunction.leftMap(onLeft)))

  private[ApiDSL] def fromEither[A, B](onLeft: B => ApiError)(either: Either[B, A])(implicit ec: ExecutionContext): Step[A] =
    EitherT[Future, ApiError, A](Future.successful(either.fold(onLeft andThen \/.left, \/.right)))

  private[ApiDSL] def fromBoolean(onFalse: => ApiError)(boolean: Boolean): Step[Unit] =
    EitherT[Future, ApiError, Unit](Future.successful(if (boolean) ().right else onFalse.left))

  private[ApiDSL] def fromTry[A](onFailure: Throwable => ApiError)(tryValue: Try[A]): Step[A] =
    EitherT[Future, ApiError, A](Future.successful(tryValue match {
      case Failure(t) => onFailure(t).left
      case Success(v) => v.right
    }))

  trait StepOps[A, B] {
    def orFailWith(failureHandler: B => ApiError): Step[A]
    def ?|(failureHandler: B => ApiError): Step[A] = orFailWith(failureHandler)
    //def ?|(failureThunk: => String): Step[A] = orFailWith(x => errorInternal(failureThunk))
    def ?|(): Step[A] = orFailWith {
      case err: Throwable => errorInternal(err.getMessage)
      case fail: Fail => errorInternal(fail.message)
      case b => errorInternal(b.toString)
    }
  }

  import scala.language.implicitConversions

  //val executionContext: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext

  implicit def futureIsAFunctor(implicit ec: ExecutionContext) = new Functor[Future] {
    override def map[A, B](fa: Future[A])(f: (A) => B) = fa.map(f)
  }

  implicit def futureIsAMonad(implicit ec: ExecutionContext) = new Monad[Future] {
    override def point[A](a: => A) = Future(a)

    override def bind[A, B](fa: Future[A])(f: (A) => Future[B]) = fa.flatMap(f)
  }

  // This instance is needed to enable filtering/pattern-matching in for-comprehensions
  // It is acceptable for pattern-matching
  implicit val failIsAMonoid = new Monoid[ApiError] {
    override def zero = errorInternal

    override def append(f1: ApiError, f2: => ApiError) = throw new IllegalStateException("should not happen")
  }

  implicit def futureToStepOps[A](future: Future[A])(implicit ec: ExecutionContext): StepOps[A, Throwable] = new StepOps[A, Throwable] {
    override def orFailWith(failureHandler: (Throwable) => ApiError) = fromFuture(failureHandler)(future)
  }

  implicit def fOptionToStepOps[A](fOption: Future[Option[A]])(implicit ec: ExecutionContext): StepOps[A, Unit] = new StepOps[A, Unit] {
    override def orFailWith(failureHandler: Unit => ApiError) = fromFOption(failureHandler(()))(fOption)
  }

  implicit def fEitherToStepOps[A, B](fEither: Future[Either[B, A]])(implicit ec: ExecutionContext): StepOps[A, B] = new StepOps[A, B] {
    override def orFailWith(failureHandler: (B) => ApiError) = fromFEither(failureHandler)(fEither)
  }

  implicit def fDisjunctionToStepOps[A, B](fDisjunction: Future[B \/ A])(implicit ec: ExecutionContext): StepOps[A, B] = new StepOps[A, B] {
    override def orFailWith(failureHandler: (B) => ApiError) = fromFDisjunction(failureHandler)(fDisjunction)
  }

  implicit def optionToStepOps[A](option: Option[A]): StepOps[A, Unit] = new StepOps[A, Unit] {
    override def orFailWith(failureHandler: (Unit) => ApiError) = fromOption(failureHandler(()))(option)
  }

  implicit def disjunctionToStepOps[A, B](disjunction: B \/ A)(implicit ec: ExecutionContext): StepOps[A, B] = new StepOps[A, B] {
    override def orFailWith(failureHandler: (B) => ApiError) = fromDisjunction(failureHandler)(disjunction)
  }

  implicit def eitherToStepOps[A, B](either: Either[B, A])(implicit ec: ExecutionContext): StepOps[A, B] = new StepOps[A, B] {
    override def orFailWith(failureHandler: (B) => ApiError) = fromEither(failureHandler)(either)
  }

  implicit def booleanToStepOps(boolean: Boolean): StepOps[Unit, Unit] = new StepOps[Unit, Unit] {
    override def orFailWith(failureHandler: (Unit) => ApiError) = fromBoolean(failureHandler(()))(boolean)
  }

  implicit def tryToStepOps[A](tryValue: Try[A]): StepOps[A, Throwable] = new StepOps[A, Throwable] {
    override def orFailWith(failureHandler: (Throwable) => ApiError) = fromTry(failureHandler)(tryValue)
  }

  // implicit def stepToResponse[R <: ApiResponse](step: Step[R])(
  //   implicit
  //   ec: ExecutionContext
  // ): Future[ApiResult] =
  //   step.run.map(_.merge)

  //implicit def stepToResponse(step: Step[ApiResponse]): Future[ApiResult] = step.run.map(_.toEither.merge)(executionContext)

  implicit def stepToResult(step: Step[Future[ApiResult]])(implicit ec: ExecutionContext): Future[ApiResult] =
    step.run.map(_.toEither match {
      case Left(e) => Future.successful(e)
      case Right(f) => f
    }).flatMap(x => x)

  implicit def stepToErrorResult(step: Step[ApiError])(implicit ec: ExecutionContext): Future[ApiError] = step.run.map(_.toEither.merge)

  implicit def stepToEither[A](step: Step[A])(implicit ec: ExecutionContext): Future[Either[ApiError, A]] = step.run.map(_.toEither)

  implicit def stepToDisjonction[A](step: Step[A]): Future[\/[ApiError, A]] = step.run

}
