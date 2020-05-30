package br.com.diegosilva.home.repositories

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

case class Device(id: Int, name: String)

class DeviceTable(tag: Tag) extends Table[(Int, String)](tag, "Devices") {

  def id: Rep[Int] = column[Int]("id", O.PrimaryKey)

  def name: Rep[String] = column[String]("name")

  def * : ProvenShape[(Int, String)] = (id, name)

}

object DeviceRepo{
  val devices = TableQuery[DeviceTable]
}
