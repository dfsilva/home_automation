package br.com.diegosilva.home.repositories

import java.time.LocalDateTime

import br.com.diegosilva.home.database.PostgresProfile.api._
import slick.lifted.ProvenShape

case class AuthToken(userId: String, token: String, created: LocalDateTime, expires: LocalDateTime)

class AuthTokenTable(tag: Tag) extends Table[AuthToken](tag, Some("housepy"), "auth_tokens") {

  def userId: Rep[String] = column[String]("user_id", O.PrimaryKey)

  def token: Rep[String] = column[String]("token")

  def created: Rep[LocalDateTime] = column[LocalDateTime]("created")

  def expires: Rep[LocalDateTime] = column[LocalDateTime]("expires")

  def * : ProvenShape[AuthToken] = (userId, token, created, expires) <> (AuthToken.tupled, AuthToken.unapply)
}

object AuthTokenRepo {
  val authTokens = TableQuery[AuthTokenTable]

  def getByToken(token: String): DBIO[Option[AuthToken]] = {
    authTokens.filter(_.token === token).result.headOption
  }

  def add(authToken: AuthToken): DBIO[AuthToken] = {
    authTokens returning authTokens += authToken
  }
}