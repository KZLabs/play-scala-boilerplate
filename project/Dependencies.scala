import sbt._
import Keys._

object Dependencies {

  val forkliftVersion = "0.2.3"

  val commonDependencies: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
    "ch.qos.logback" % "logback-classic" % "1.1.7"
  )

  val dbAccess: Seq[ModuleID] = Seq(
    "io.getquill" %% "quill-jdbc" % "1.1.0",
    "mysql" % "mysql-connector-java" % "5.1.34"
  )

  val storageProdDependencies : Seq[ModuleID] = commonDependencies ++ dbAccess

  val coreDependencies : Seq[ModuleID] = commonDependencies ++ {
    Seq(
      "com.github.t3hnar" %% "scala-bcrypt" % "3.0",
      "net.codingwell" %% "scala-guice" % "4.1.0"
    )
  }

  val apiDependencies : Seq[ModuleID] = commonDependencies ++ dbAccess ++ {
    Seq(
      "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
      "net.codingwell" %% "scala-guice" % "4.1.0",
      "com.pauldijou" %% "jwt-play-json" % "0.11.0",
      "com.typesafe.play" %% "play-json" % "2.5.12"
      // "com.netaporter" %% "scala-uri" % "0.4.14",
      // "net.codingwell" %% "scala-guice" % "4.1.0"
      //jdbc,
      //cache,
      // ws
      //specs2 % Test
    )

  }
}
