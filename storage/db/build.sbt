name := "migrations"
scalaVersion := "2.11.8"
version := "0.1.0"
organization := "com.github.dolcalmi"

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.imageworks.scala-migrations" %% "scala-migrations" % "1.1.1",
  "org.rogach" %% "scallop" % "2.1.0",
  "com.typesafe" % "config" % "1.3.0",
  "mysql" % "mysql-connector-java" % "5.1.34"
)

sourcesInBase := false
resourceDirectory in Compile := baseDirectory.value / "config"
unmanagedSourceDirectories in Compile += baseDirectory.value / "utils"
unmanagedSourceDirectories in Compile += baseDirectory.value / "migrations"
