package br.com.diegosilva.home.data

import scala.util.Try

object Lecture {
  def fromIotMessage(message: IOTMessage): Try[Lecture] = Try(Lecture(address = message.id, sensorType = message.sensor.substring(0, 2), sensorNumber = Integer.parseInt(message.sensor.substring(2)), value = message.value))
}

case class Lecture(address: String, sensorNumber: Int, sensorType: String, value: String)


