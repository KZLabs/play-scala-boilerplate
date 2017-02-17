import sbt._
import Keys._

object Dependencies {

  val forkliftVersion = "0.2.3"

  val commonDependencies: Seq[ModuleID] = Seq(
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  )

  val slick: Seq[ModuleID] = Seq(
    "com.typesafe.slick" %% "slick" % "3.1.1",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
    "mysql" % "mysql-connector-java" % "5.1.34"
  )

  val forkliftDependencies: Seq[ModuleID] = Seq(
    "com.liyaos" %% "scala-forklift-slick" % forkliftVersion,
    "io.github.nafg" %% "slick-migration-api" % "0.3.0"
  )

  // val json : Seq[ModuleID] = Seq(
  //     "io.argonaut" %% "argonaut" % "6.0.4",
  //     "com.propensive" %% "rapture-json-argonaut" % "1.1.0",
  //     "com.typesafe.play" %% "play-json" % "2.5")

  val storageProdDependencies : Seq[ModuleID] = commonDependencies ++ slick

  val migrationsDependencies : Seq[ModuleID] = commonDependencies ++ slick ++ forkliftDependencies
  val migrationManagerDependencies : Seq[ModuleID] = commonDependencies ++ slick ++ forkliftDependencies

  val coreDependencies : Seq[ModuleID] = commonDependencies ++ {
    Seq(
      "com.github.t3hnar" %% "scala-bcrypt" % "3.0",
      "net.codingwell" %% "scala-guice" % "4.1.0"
    )
  }

  val silhouetteVer = "4.0.0"
  val silhouetteLib : Seq[ModuleID] = Seq(
    "com.mohiva" %% "play-silhouette" % silhouetteVer,
    "com.mohiva" %% "play-silhouette-password-bcrypt" % silhouetteVer,
    "com.mohiva" %% "play-silhouette-crypto-jca" % silhouetteVer,
    "com.mohiva" %% "play-silhouette-persistence" % silhouetteVer,
    "com.mohiva" %% "play-silhouette-testkit" % silhouetteVer % "test"
  )

  val apiDependencies    : Seq[ModuleID] = commonDependencies ++ slick ++ silhouetteLib ++ {
    Seq(
      "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
      "net.codingwell" %% "scala-guice" % "4.1.0"
      // "com.netaporter" %% "scala-uri" % "0.4.14",
      // "net.codingwell" %% "scala-guice" % "4.1.0"
      //jdbc,
      //cache,
      // ws
      //specs2 % Test
    )

  }
}
