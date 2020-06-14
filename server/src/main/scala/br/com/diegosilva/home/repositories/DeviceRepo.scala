package br.com.diegosilva.home.repositories

import br.com.diegosilva.home.data.DeviceType
import br.com.diegosilva.home.database.PostgresProfile.api._
import slick.lifted.ProvenShape
import collection._

case class Device(id: Option[Int] = None, address: Int, name: String, devType: DeviceType.Value, owner: String)

case class DeviceSensors(device: Device, sensors: Seq[String])

class DeviceTable(tag: Tag) extends Table[Device](tag, Some("housepy"), "devices") {

  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def address: Rep[Int] = column[Int]("address")

  def name: Rep[String] = column[String]("name")

  def devType: Rep[DeviceType.Value] = column[DeviceType.Value]("type")

  def owner: Rep[String] = column[String]("owner")

  def * : ProvenShape[Device] = (id.?, address, name, devType, owner) <> (Device.tupled, Device.unapply)

}

object DeviceRepo {
  val devices = TableQuery[DeviceTable]
  val sensors = TableQuery[SensorTable]

  def add(device: Device): DBIO[Device] = {
    devices returning devices += device
  }

  def devicesByUser(userId: String): DBIO[Seq[Device]] = {
    devices.filter(_.owner === userId).sortBy(_.name).result
  }

  def devicesAndSensorsByUser(userId: String): DBIO[Seq[(Device, Sensor)]] = {
    (for {
      devices <- devices.filter(_.owner === userId).sortBy(_.name)
      sensors <- sensors if (devices.id === sensors.deviceId)
    } yield (devices, sensors)).result
  }
}
