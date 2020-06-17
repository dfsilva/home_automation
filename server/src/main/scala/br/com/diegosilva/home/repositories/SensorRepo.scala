package br.com.diegosilva.home.repositories

import br.com.diegosilva.home.database.PostgresProfile.api._
import slick.lifted.ProvenShape

import scala.concurrent.duration.Duration

case class Trigger(id: Int, sensorId: Int, toSensorId: Int, activateValue: Any, setValue: Any, duration: Option[Duration])

class TriggerTable(tag: Tag) extends Table[Trigger](tag, Some("housepy"), "triggers") {

  def id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)

  def sensorId: Rep[Int] = column[Int]("sensor_id")

  def toSensorId: Rep[Int] = column[Int]("to_sensor_id")

  def activateValue: Rep[Any] = column[Any]("activate_value")

  def setValue: Rep[Any] = column[Any]("set_value")

  def duration: Rep[Option[Duration]] = column[Option[Duration]]("duration")

  def * : ProvenShape[Trigger] = (id, sensorId, toSensorId, activateValue, setValue, duration) <> (Trigger.tupled, Trigger.unapply)
}

case class Command(id: Int, sensorId: Int, value: String, description: String)

class CommandTable(tag: Tag) extends Table[Command](tag, Some("housepy"), "commands") {

  def id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)

  def sensorId: Rep[Int] = column[Int]("sensor_id")

  def value: Rep[String] = column[String]("value")

  def description: Rep[String] = column[String]("description")

  def * : ProvenShape[Command] = (id, sensorId, value, description) <> (Command.tupled, Command.unapply)

}

case class Sensor(number: Int, sensorType: String, deviceAddress: String, dataType: String, name: String, position: Int)

class SensorTable(tag: Tag) extends Table[Sensor](tag, Some("housepy"), "sensors") {

  def number: Rep[Int] = column[Int]("number")

  def sensorType: Rep[String] = column[String]("type")

  def deviceAddress: Rep[String] = column[String]("device_address")

  def dataType: Rep[String] = column[String]("data_type")

  def name: Rep[String] = column[String]("name")

  def position: Rep[Int] = column[Int]("position")

  def * : ProvenShape[Sensor] = (number, sensorType, deviceAddress, dataType, name, position) <> (Sensor.tupled, Sensor.unapply)

  def pk = primaryKey("pk_sensor", (number, sensorType, deviceAddress))

}

object SensorRepo {

  val sensors = TableQuery[SensorTable]
  val commands = TableQuery[CommandTable]
  val triggers = TableQuery[TriggerTable]

  def sensorsWithAll(address: String): DBIO[Seq[(Sensor, Command, Trigger)]] = {
    (for {
      s <- sensors.filter(_.deviceAddress === address)
      c <- commands if s.number === c.sensorId
      t <- triggers if s.number === t.sensorId
    } yield (s, c, t)).result
  }

}
