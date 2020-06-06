package br.com.diegosilva.home.repositories

import br.com.diegosilva.home.data.DeviceType
import br.com.diegosilva.home.database.PostgresProfile.api._
import slick.lifted.ProvenShape

case class Device(id: Option[Int] = None, name: String, devType: DeviceType.Value, owner: String)

class DeviceTable(tag: Tag) extends Table[Device](tag, Some("housepy"), "devices") {

  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def name: Rep[String] = column[String]("name")

  def devType: Rep[DeviceType.Value] = column[DeviceType.Value]("type")

  def owner: Rep[String] = column[String]("owner")

  def * : ProvenShape[Device] = (id.?, name, devType, owner) <> (Device.tupled, Device.unapply)

}

object DeviceRepo {
  val devices = TableQuery[DeviceTable]

  def add(device: Device): DBIO[Device] = {
    devices returning devices += device
  }
}
