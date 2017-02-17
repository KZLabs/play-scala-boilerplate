import com.liyaos.forklift.slick._
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

object MyMigrations extends App
    with SlickMigrationCommandLineTool
    with SlickMigrationCommands
    with SlickMigrationManager
    with Codegen {

      override lazy val dbConfig = DatabaseConfig.forConfig[JdbcProfile]("migrations.database")

  MigrationSummary
  execCommands(args.toList)
}
