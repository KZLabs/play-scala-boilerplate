//https://github.com/aaronp/multi-project

import Dependencies._

name := Common.appNamePrefix + "root"

version := Common.appVersion

scalaVersion := Common.appScalaVersion

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

lazy val core = project.
settings(libraryDependencies ++= Dependencies.coreDependencies)
enablePlugins(Common)

lazy val api = project.
dependsOn(core % "test->test;compile->compile").
settings(libraryDependencies ++= Dependencies.apiDependencies).
enablePlugins(Common, PlayScala)

lazy val root = (project in file(".")).
aggregate(core, api)
