//https://github.com/aaronp/multi-project

import Dependencies._

scalaVersion := "2.11.8"

name := Common.appNamePrefix + "root"

version := Common.appVersion

lazy val core = project.
settings(libraryDependencies ++= Dependencies.coreDependencies).
enablePlugins(Common)

lazy val storageProd = (project in file("storage/production")).
dependsOn(core % "test->test;compile->compile").
settings(libraryDependencies ++= Dependencies.storageProdDependencies).
enablePlugins(Common)

lazy val api = project.
dependsOn(storageProd, core % "test->test;compile->compile").
settings(libraryDependencies ++= Dependencies.apiDependencies).
enablePlugins(Common, PlayScala)

lazy val root = (project in file(".")).
aggregate(storageProd, core, api)

// Run play server
addCommandAlias("s", "api/run")
// Run Main class in storage prod for run migration tasks
addCommandAlias("db", "storageProd/run")
