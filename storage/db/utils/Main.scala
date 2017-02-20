package com.github.dolcalmi.migrations.utils

import java.io._
import scala.language.reflectiveCalls

object Main extends App{

  val resourcesPath = getClass.getResource("/migrations.conf")
  val migrator = new DbMigrator(new File(resourcesPath.getPath))

  val conf = new MigrationCommands(args.toSeq)

  conf.subcommand match {
    case Some(conf.newCommand) =>
      if (conf.newCommand.opts.isSupplied)
        migrator.createMigration(conf.newCommand.opts())

    case Some(conf.migrateCommand) =>
      migrator.migrate(conf.migrateCommand.opts.toOption)

    case Some(conf.rollbackCommand) =>
      migrator.rollback(conf.rollbackCommand.opts.toOption)

    case Some(conf.resetCommand) =>
      migrator.reset()

    case _ =>
  }
}
