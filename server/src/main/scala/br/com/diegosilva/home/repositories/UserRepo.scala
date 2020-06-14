package br.com.diegosilva.home.repositories

import br.com.diegosilva.home.database.PostgresProfile.api._
import slick.lifted.ProvenShape

case class User(uid: String, name: String, email: String)

class UserTable(tag: Tag) extends Table[User](tag, Some("housepy"), "users") {

  def uid: Rep[String] = column[String]("uid", O.PrimaryKey)

  def name: Rep[String] = column[String]("name")

  def email: Rep[String] = column[String]("email")

  def * : ProvenShape[User] = (uid, name, email) <> (User.tupled, User.unapply)
}

object UserRepo {
  val users = TableQuery[UserTable]

  def load(uid: String): DBIO[Option[User]] = {
    users.filter(_.uid === uid).result.headOption
  }
}