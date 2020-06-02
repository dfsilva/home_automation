package br.com.diegosilva.home.repositories

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

case class Device(id: Int, name: String, devType: String )

class DeviceTable(tag: Tag) extends Table[Device](tag, "Devices") {

  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def name: Rep[String] = column[String]("name")

  def devType: Rep[String] = column[String]("type")

  def * : ProvenShape[Device] = (id, name, devType) <> (Device.tupled, Device.unapply)

}

object DeviceRepo{
  val devices = TableQuery[DeviceTable]
}
