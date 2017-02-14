//https://github.com/aaronp/multi-project

import Dependencies._

name := Common.appNamePrefix + "root"

version := Common.appVersion

scalaVersion := Common.appScalaVersion

lazy val core = project.
settings(libraryDependencies ++= Dependencies.coreDependencies)
enablePlugins(Common)

lazy val storageProd = (project in file("storage/production")).
dependsOn(core % "test->test;compile->compile").
settings(libraryDependencies ++= Dependencies.storageProdDependencies)
enablePlugins(Common)

lazy val api = project.
dependsOn(storageProd, core % "test->test;compile->compile").
settings(libraryDependencies ++= Dependencies.apiDependencies).
enablePlugins(Common, PlayScala)

lazy val root = (project in file(".")).
aggregate(storageProd, core, api)
