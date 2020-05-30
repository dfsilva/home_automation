package br.com.diegosilva.home.repositories

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

case class User(uid: String, name:String, email:String)

class UserTable(tag: Tag) extends Table[User](tag, "Users") {

  def uid: Rep[String] = column[String]("uid", O.PrimaryKey)
  def name: Rep[String] = column[String]("name")
  def email: Rep[String] = column[String]("email")

  def * : ProvenShape[User] = (uid, name, email).mapTo[User]
}

object UserRepo{
  val users = TableQuery[UserTable]
}