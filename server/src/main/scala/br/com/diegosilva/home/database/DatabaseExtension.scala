package br.com.diegosilva.home.database

import akka.actor.typed.{ActorSystem, Extension, ExtensionId}
import org.flywaydb.core.Flyway
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.hikaricp.HikariCPJdbcDataSource

import scala.util.{Failure, Success, Try}

class DatabasePoolExtensionImpl(system: ActorSystem[_], val database: Database) extends Extension {
  def connection(): Database = database
}

object DatabasePool extends ExtensionId[DatabasePoolExtensionImpl] {
  def createExtension(system: ActorSystem[_]): DatabasePoolExtensionImpl = {
    val db = initDb()

    val flyway = Flyway.configure()
      .schemas("housepy")
      .defaultSchema("housepy")
      .dataSource(db.source.asInstanceOf[HikariCPJdbcDataSource].ds).load()

    flyway.migrate()

    system.whenTerminated.andThen(_ => {
      db.close()
    })(system.executionContext)

    val ext = new DatabasePoolExtensionImpl(system, db)

    Try(flyway.migrate()) match {
      case Success(_) =>
      case Failure(e) =>
        system.log.error("Migration failed", e)
    }
    ext
  }

  private def initDb(): Database = {
    Database.forConfig("database")
  }

  def get(system: ActorSystem[_]): DatabasePoolExtensionImpl = apply(system)
}
