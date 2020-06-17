package br.com.diegosilva.home.repositories

import br.com.diegosilva.home.data.DeviceType
import br.com.diegosilva.home.database.PostgresProfile.api._
import slick.lifted.ProvenShape
import collection._

case class Device(address: String, name: String, devType: DeviceType.Value, owner: String, position: Int)

case class DeviceSensors(device: Device, sensors: Seq[Sensor])

class DeviceTable(tag: Tag) extends Table[Device](tag, Some("housepy"), "devices") {

  def address: Rep[String] = column[String]("address", O.PrimaryKey)

  def name: Rep[String] = column[String]("name")

  def devType: Rep[DeviceType.Value] = column[DeviceType.Value]("type")

  def owner: Rep[String] = column[String]("owner")

  def position: Rep[Int] = column[Int]("position")

  def * : ProvenShape[Device] = (address, name, devType, owner, position) <> (Device.tupled, Device.unapply)

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
      sensors <- sensors if devices.address === sensors.deviceAddress
    } yield (devices, sensors)).result
  }
}
