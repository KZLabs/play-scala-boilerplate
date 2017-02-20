package com.github.dolcalmi.migrations.utils

import java.io._
import scala.sys
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.imageworks.migration._

import com.typesafe.scalalogging._
import com.typesafe.config.ConfigFactory

class DbMigrator(configFile: File) extends LazyLogging {

  val env = sys.props.getOrElse("SCALA_ENV", sys.env.getOrElse("SCALA_ENV", "development"))

  logger.info(s"Loading config file: $configFile for environment: $env")

  val migrationsConfig = ConfigFactory.parseFile(configFile).resolve().getConfig(env)

  val migrationsPackage = migrationsConfig.getString("package")
  val migrationsDir = migrationsConfig.getString("directory")

  val driverClassName = migrationsConfig.getString("db.driver")
  val dbUrl = migrationsConfig.getString("db.url")
  val dbUser = migrationsConfig.getString("db.user")
  val dbPassword = migrationsConfig.getString("db.password")

  val vendor = Vendor.forDriver(driverClassName)
  val databaseAdapter = DatabaseAdapter.forVendor(vendor, None)
  val connectionBuilder = new ConnectionBuilder(dbUrl, dbUser, dbPassword)

  val migrator = new Migrator(connectionBuilder, databaseAdapter)

  Class.forName(driverClassName)

  def migrate(version: Option[Long]): Boolean = {

    if (version.isEmpty){
      logger.info(s"Migrating...")
      migrator.migrate(InstallAllMigrations, migrationsPackage, false)
    } else {
      logger.info(s"Migrating to version ${version}")
      migrator.migrate(MigrateToVersion(version.get), migrationsPackage, false)
    }
    logger.info("Done!")
    true
  }

  def rollback(count: Option[Int]): Boolean = {
    val numRollbacks = count.getOrElse(1)
    if (numRollbacks == 1)
      logger.info(s"Rollback last migration...")
    else
      logger.info(s"Rollback ${numRollbacks} migration(s)...")

    migrator.migrate(RollbackMigration(numRollbacks), migrationsPackage, false)
    logger.info("Done!")
    true
  }

  def reset(): Boolean = {
    logger.info("Reseting database...")
    migrator.migrate(RemoveAllMigrations, migrationsPackage, false)
    logger.info("Done!")
    true
  }

  def createMigration(name: String): Boolean = {
    val now = LocalDateTime.now
    val creationDate: String = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    val className: String = s"Migrate_${creationDate}_$name"
    val migrationFileName: String = s"${migrationsDir}/${className}.scala"
    logger.info(s"Creating migration file: $migrationFileName")
    val migrationTemplate: String = s"""
package ${migrationsPackage}

import com.imageworks.migration._

/**
 * authoredAt: ${System.currentTimeMillis}
 */

class ${className} extends Migration
{
  def up() {

  }

  def down() {

  }
}
    """
    new PrintWriter(migrationFileName) {
      write(migrationTemplate)
      close()
    }
    logger.info(s"Created migration '$migrationFileName'")
    true
  }
}
