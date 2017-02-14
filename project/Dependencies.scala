import sbt._
import Keys._

object Dependencies {

  val commonDependencies: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  )

  // val json : Seq[ModuleID] = Seq(
  //     "io.argonaut" %% "argonaut" % "6.0.4",
  //     "com.propensive" %% "rapture-json-argonaut" % "1.1.0",
  //     "com.typesafe.play" %% "play-json" % "2.5")

  val coreDependencies : Seq[ModuleID] = commonDependencies

  val apiDependencies    : Seq[ModuleID] = commonDependencies ++ {
    Seq(
      "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
      // "com.netaporter" %% "scala-uri" % "0.4.14",
      // "net.codingwell" %% "scala-guice" % "4.1.0"
      //jdbc,
      //cache,
      // ws
      //specs2 % Test
    )

  }
}
