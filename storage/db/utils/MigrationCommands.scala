package com.github.dolcalmi.migrations.utils

import org.rogach.scallop._
import com.typesafe.scalalogging._
import scala.language.reflectiveCalls

class MigrationCommands(arguments: scala.collection.Seq[String]) extends ScallopConf(arguments) with LazyLogging{
  banner("""Usage: [new|migrate|rollback|reset]""".stripMargin)


  val newCommand = new Subcommand("new") {
    banner("""Usage: new MIGRATION_NAME
             |Creates new migration file
           """.stripMargin)
    val opts = trailArg[String]()
  }

  val migrateCommand = new Subcommand("migrate") {
    banner("""Usage: migrate [MigrationId]
             |Migrates to the last migration or to the specified MigrationId
           """.stripMargin)
    val opts = trailArg[Long]("version",required = false)
  }

  val rollbackCommand = new Subcommand("rollback") {
    banner("""Usage: rollback [Count]
             |Rollback the last migration or the specified count of migrations
           """.stripMargin)
    val opts = trailArg[Int]("numberOfMigrations", required = false)
  }

  val resetCommand = new Subcommand("reset") {
    banner("""Usage: reset
             |Restore the database to the original status
           """.stripMargin)
  }
  // val migrate = trailArg[Long](required)
  // val rollback = trailArg[Int]()
  addSubcommand(newCommand)
  addSubcommand(migrateCommand)
  addSubcommand(rollbackCommand)
  addSubcommand(resetCommand)
  verify()
}
