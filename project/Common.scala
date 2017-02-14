import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

/**
* Settings that are comment to all the SBT projects
*/
object Common extends AutoPlugin {

  val appNamePrefix = "MyProject-"
  val appOrganization = "me.kzlabs"
  val appVersion = "0.0.1"
  val appScalaVersion = "2.11.8"

  override def trigger = allRequirements
  override def requires: sbt.Plugins = JvmPlugin

  override def projectSettings = Seq(
    organization := appOrganization,
    version := appVersion,
    scalaVersion := appScalaVersion,
    resolvers ++= Seq(
        "Atlassian Releases" at "https://maven.atlassian.com/repository/public/",
        "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
        "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
        Resolver.typesafeRepo("releases")
    ),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions ++= Seq(
      "-encoding",
      "UTF-8", // yes, this is 2 args
      "-target:jvm-1.8",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-numeric-widen",
      "-Xfatal-warnings"
    ),
    scalacOptions in Test ++= Seq("-Yrangepos"),
    autoAPIMappings := true
  )
}
