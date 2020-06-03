package br.com.diegosilva.home.repositories

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

import scala.concurrent.duration.Duration

case class Trigger(id: Int, sensorId:String, deviceId: Int, toDeviceId: Int, toSensorId: String, activateValue: Any, setValue: Any, duration: Option[Duration])

class TriggerTable(tag: Tag) extends Table[Trigger](tag, Some("housepy"),"Triggers") {

  import CustomMappings._

  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def sensorId: Rep[String] = column[String]("sensor_id", O.PrimaryKey)

  def deviceId: Rep[Int] = column[Int]("device_id", O.PrimaryKey)

  def toDeviceId: Rep[Int] = column[Int]("to_device_id")

  def toSensorId: Rep[String] = column[String]("to_sensor_id")

  def activateValue: Rep[Any] = column[Any]("activate_value")

  def setValue: Rep[Any] = column[Any]("set_value")

  def duration: Rep[Option[Duration]] = column[Option[Duration]]("duration")

  def * : ProvenShape[Trigger] = (id, sensorId, deviceId, toDeviceId, toSensorId, activateValue, setValue, duration) <> (Trigger.tupled, Trigger.unapply)
}

case class Command(id: Int, sensorId:String, deviceId: Int, value: String, description: String)

class CommandTable(tag: Tag) extends Table[Command](tag, Some("housepy"),"Commands") {

  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def sensorId: Rep[String] = column[String]("sensor_id", O.PrimaryKey)

  def deviceId: Rep[Int] = column[Int]("device_id", O.PrimaryKey)

  def value: Rep[String] = column[String]("value")

  def description: Rep[String] = column[String]("description")

  def * : ProvenShape[Command] = (id, sensorId, deviceId, value, description) <> (Command.tupled, Command.unapply)
}

case class Sensor(id: String, deviceId: Int, dataType: String, triggers: Seq[Trigger] = Seq(), commands: Seq[Command] = Seq())

class SensorTable(tag: Tag) extends Table[(String, Int)](tag, Some("housepy"),"Sensors") {

  def id: Rep[String] = column[String]("id", O.PrimaryKey, O.AutoInc)

  def deviceId: Rep[Int] = column[Int]("device_id", O.PrimaryKey)

  def dataType: Rep[String] = column[String]("data_type")

  def * : ProvenShape[(String, Int)] = (id, deviceId)
}

object SensorRepo {

  val sensors = TableQuery[SensorTable]
  val commands = TableQuery[CommandTable]
  val triggers = TableQuery[TriggerTable]

  def sensorsWithAll(deviceId: Int){
    (for {
      s <- sensors.filter(_.deviceId === deviceId)
      c <- commands if (s.deviceId === c.deviceId && s.id === c.sensorId)
      t <- triggers if (s.deviceId === t.deviceId && s.id === t.sensorId)
    }yield (s, c, t))
  }

}
