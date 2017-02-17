import slick.driver.MySQLDriver.api._
import com.liyaos.forklift.slick.DBIOMigration
import storage.production.tables.Users

object M1 {
  val schema = TableQuery[Users].schema
  MyMigrations.migrations = MyMigrations.migrations :+ DBIOMigration(1)(
    DBIO.seq(
      schema.create
    ))
}
