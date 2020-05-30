package br.com.diegosilva.home.database

import akka.actor.typed.{ActorSystem, Extension, ExtensionId}
import slick.jdbc.PostgresProfile.api._

class DatabasePool(system: ActorSystem[_]) extends Extension {

  lazy val _db: Database = Database.forConfig("database")

  def connection(): Database = _db
}

object DatabasePool extends ExtensionId[DatabasePool] {
  def createExtension(system: ActorSystem[_]): DatabasePool = new DatabasePool(system)

  def get(system: ActorSystem[_]): DatabasePool = apply(system)
}
