package core.utils

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.control.NonFatal
import scala.util.{ Failure, Success, Try }
import scalaz.syntax.either._
import scalaz.syntax.std.option._
import scalaz._

import core.entities.Fail

/**
 * Inspiration :
 *  - http://fr.slideshare.net/normation/nrm-scala-iocorrectlymanagingerrorsinscalav13
 *  - https://github.com/Kanaka-io/play-monadic-actions
 */
object FailDSL {

  type Step[A] = EitherT[Future, Fail, A]

  private[FailDSL] def fromFuture[A](onFailure: Throwable => Fail)(future: Future[A])(implicit ec: ExecutionContext): Step[A] = {
    EitherT[Future, Fail, A](
      future.map(_.right).recover {
        case NonFatal(t) => onFailure(t).left
      }
    )
  }

  private[FailDSL] def fromFOption[A](onNone: => Fail)(fOption: Future[Option[A]])(implicit ec: ExecutionContext): Step[A] =
    EitherT[Future, Fail, A](
      fOption.map(_ \/> onNone).recover {
        case NonFatal(t) => onNone.withEx(t).left
      }
    )

  private[FailDSL] def fromFEither[A, B](onLeft: B => Fail)(fEither: Future[Either[B, A]])(implicit ec: ExecutionContext): Step[A] = {
    EitherT[Future, Fail, A](fEither.map(_.fold(onLeft andThen \/.left, \/.right)))
  }

  private[FailDSL] def fromFDisjunction[A, B](onLeft: B => Fail)(fDisjunction: Future[B \/ A])(implicit ec: ExecutionContext): Step[A] =
    EitherT[Future, Fail, A](fDisjunction.map(_.leftMap(onLeft)))

  private[FailDSL] def fromOption[A](onNone: => Fail)(option: Option[A]): Step[A] =
    EitherT[Future, Fail, A](Future.successful(option \/> onNone))

  private[FailDSL] def fromDisjunction[A, B](onLeft: B => Fail)(disjunction: B \/ A)(implicit ec: ExecutionContext): Step[A] =
    EitherT[Future, Fail, A](Future.successful(disjunction.leftMap(onLeft)))

  private[FailDSL] def fromEither[A, B](onLeft: B => Fail)(either: Either[B, A])(implicit ec: ExecutionContext): Step[A] =
    EitherT[Future, Fail, A](Future.successful(either.fold(onLeft andThen \/.left, \/.right)))

  private[FailDSL] def fromBoolean(onFalse: => Fail)(boolean: Boolean): Step[Unit] =
    EitherT[Future, Fail, Unit](Future.successful(if (boolean) ().right else onFalse.left))

  private[FailDSL] def fromTry[A](onFailure: Throwable => Fail)(tryValue: Try[A]): Step[A] =
    EitherT[Future, Fail, A](Future.successful(tryValue match {
      case Failure(t) => onFailure(t).left
      case Success(v) => v.right
    }))

  trait StepOps[A, B] {
    def orFailWith(failureHandler: B => Fail): Step[A]
    def ?|(failureHandler: B => Fail): Step[A] = orFailWith(failureHandler)
    def ?|(failureThunk: => String): Step[A] = orFailWith {
      case err: Throwable => Fail(failureThunk).withEx(err)
      case fail: Fail => Fail(failureThunk).withEx(fail)
      case b => Fail(b.toString).withEx(failureThunk)
    }
    def ?|(): Step[A] = orFailWith {
      case err: Throwable => Fail("Unexpected exception").withEx(err)
      case fail: Fail => fail
      case b => Fail(b.toString)
    }
  }

  import scala.language.implicitConversions

  val executionContext: ExecutionContext = ExecutionContext.global

  implicit val futureIsAFunctor = new Functor[Future] {
    override def map[A, B](fa: Future[A])(f: (A) => B) = fa.map(f)(executionContext)
  }

  implicit val futureIsAMonad = new Monad[Future] {
    override def point[A](a: => A) = Future(a)(executionContext)

    override def bind[A, B](fa: Future[A])(f: (A) => Future[B]) = fa.flatMap(f)(executionContext)
  }

  // This instance is needed to enable filtering/pattern-matching in for-comprehensions
  // It is acceptable for pattern-matching
  implicit val failIsAMonoid = new Monoid[Fail] {
    override def zero = Fail("")

    override def append(f1: Fail, f2: => Fail) = throw new IllegalStateException("should not happen")
  }

  implicit def futureToStepOps[A](future: Future[A]): StepOps[A, Throwable] = new StepOps[A, Throwable] {
    override def orFailWith(failureHandler: (Throwable) => Fail) = fromFuture(failureHandler)(future)(executionContext)
  }

  implicit def fOptionToStepOps[A](fOption: Future[Option[A]]): StepOps[A, Unit] = new StepOps[A, Unit] {
    override def orFailWith(failureHandler: Unit => Fail) = fromFOption(failureHandler(()))(fOption)(executionContext)
  }

  implicit def fEitherToStepOps[A, B](fEither: Future[Either[B, A]]): StepOps[A, B] = new StepOps[A, B] {
    override def orFailWith(failureHandler: (B) => Fail) = fromFEither(failureHandler)(fEither)(executionContext)
  }

  implicit def fDisjunctionToStepOps[A, B](fDisjunction: Future[B \/ A]): StepOps[A, B] = new StepOps[A, B] {
    override def orFailWith(failureHandler: (B) => Fail) = fromFDisjunction(failureHandler)(fDisjunction)(executionContext)
  }

  implicit def optionToStepOps[A](option: Option[A]): StepOps[A, Unit] = new StepOps[A, Unit] {
    override def orFailWith(failureHandler: (Unit) => Fail) = fromOption(failureHandler(()))(option)
  }

  implicit def disjunctionToStepOps[A, B](disjunction: B \/ A): StepOps[A, B] = new StepOps[A, B] {
    override def orFailWith(failureHandler: (B) => Fail) = fromDisjunction(failureHandler)(disjunction)(executionContext)
  }

  implicit def eitherToStepOps[A, B](either: Either[B, A]): StepOps[A, B] = new StepOps[A, B] {
    override def orFailWith(failureHandler: (B) => Fail) = fromEither(failureHandler)(either)(executionContext)
  }

  implicit def booleanToStepOps(boolean: Boolean): StepOps[Unit, Unit] = new StepOps[Unit, Unit] {
    override def orFailWith(failureHandler: (Unit) => Fail) = fromBoolean(failureHandler(()))(boolean)
  }

  implicit def tryToStepOps[A](tryValue: Try[A]): StepOps[A, Throwable] = new StepOps[A, Throwable] {
    override def orFailWith(failureHandler: (Throwable) => Fail) = fromTry(failureHandler)(tryValue)
  }

  implicit def stepToResult(step: Step[Fail]): Future[Fail] = step.run.map(_.toEither.merge)(executionContext)

  implicit def stepToEither[A](step: Step[A]): Future[Either[Fail, A]] = step.run.map(_.toEither)(executionContext)

  implicit def stepToDisjonction[A](step: Step[A]): Future[\/[Fail, A]] = step.run

}