package core.entities

//import org.apache.commons.lang3.exception.ExceptionUtils

import scalaz._

case class Fail(message: String, cause: Option[\/[Throwable, Fail]] = None) {

  def withEx(s: String) = Fail(s, Some(\/-(this)))

  def withEx(ex: Throwable) = Fail(this.message, Some(-\/(ex)))

  def withEx(fail: Fail) = Fail(this.message, Some(\/-(fail)))

  def messages(): NonEmptyList[String] = cause match {
    case None => NonEmptyList(message)
    //case Some(-\/(exp)) => message <:: NonEmptyList(s"${exp.getMessage} ${getStackTrace(exp)}")
    case Some(-\/(exp)) => message <:: NonEmptyList(s"${exp.getMessage}")
    case Some(\/-(parent)) => message <:: parent.messages
  }

  def userMessage(): String = messages.list.toNel.mkString(" <- ")

  def getRootException(): Option[Throwable] = cause flatMap {
    _ match {
      case -\/(exp) => Some(exp)
      case \/-(parent) => parent.getRootException
    }
  }

  // private def getStackTrace(e: Throwable): String = {
  //   ExceptionUtils.getStackTrace(e)
  // }
}
