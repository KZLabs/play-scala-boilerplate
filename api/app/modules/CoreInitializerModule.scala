package modules

import javax.inject._
import net.codingwell.scalaguice.ScalaModule
import com.google.inject.{ AbstractModule }
import com.google.inject.name.Names

import utils._

import play.api.Logger

/** The Guice module which wires all core context dependencies.
  */
class CoreInitializerModule extends AbstractModule with ScalaModule {

  /** Configures the module.
    */
  def configure() {

    bind[AppContext].to[AppContext]

  }
}
