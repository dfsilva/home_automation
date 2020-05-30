package br.com.diegosilva.home.repositories

import scala.concurrent.duration.Duration
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

case class Trigger(toSensorId: String, activateValue: Any, setValue: Any, duration: Duration)

class TriggerTable(tag: Tag) extends Table[Trigger](tag, "Triggers") {

  def uid: Rep[String] = column[String]("uid", O.PrimaryKey)
  def name: Rep[String] = column[String]("name")
  def email: Rep[String] = column[String]("email")

  def * : ProvenShape[Trigger] = (uid, name, email).mapTo[User]
}



case class Command(value: String)



case class Sensor(uid: String, triggers: Seq[Trigger], commands: Seq[Command])


