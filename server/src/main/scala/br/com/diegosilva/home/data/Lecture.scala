package br.com.diegosilva.home.data

import scala.util.Try

object Lecture {
  def fromIotMessage(message: IOTMessage): Try[Lecture] = Try(Lecture(address = message.id, sensorType = message.sensor.substring(0, 2), sensorId = Integer.parseInt(message.sensor.substring(2)), value = message.value))
}

case class Lecture(address: String, sensorId: Int, sensorType: String, value: String)


