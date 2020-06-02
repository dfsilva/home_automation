package br.com.diegosilva.home.database

import akka.actor.typed.{ActorSystem, Extension, ExtensionId}
import org.flywaydb.core.Flyway
import slick.jdbc.PostgresProfile.api._

class DatabasePoolExtensionImpl(system: ActorSystem[_], val database: Database) extends Extension {

  def connection(): Database = database
}

object DatabasePool extends ExtensionId[DatabasePoolExtensionImpl] {
  def createExtension(system: ActorSystem[_]): DatabasePoolExtensionImpl = {
    val db = initDb()

    val flyway = Flyway.configure.dataSource(db.source)

    val ext = new DatabasePoolExtensionImpl(system, db)

    system.whenTerminated.andThen(_ => {
      db.close()
    })(system.executionContext)

    ext
  }

  private def initDb(): Database = {
    Database.forConfig("database")
  }

  def get(system: ActorSystem[_]): DatabasePoolExtensionImpl = apply(system)
}
