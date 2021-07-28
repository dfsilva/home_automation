package br.com.diegosilva.home.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import br.com.diegosilva.home.actors.WsUserActor.{Notify, Register}
import br.com.diegosilva.home.api.AutomationRoutes.{AddDevice, SendMessage, ReceiveMessage}
import br.com.diegosilva.home.data.{DeviceType, IOTMessage, Lecture}
import br.com.diegosilva.home.repositories.{Device, DeviceSensors, Sensor, User}
import spray.json._

trait JsonFormats extends SprayJsonSupport with DefaultJsonProtocol {

  import collection.immutable._

  class EnumJsonConverter[T <: scala.Enumeration](enu: T) extends RootJsonFormat[T#Value] {
    override def write(obj: T#Value): JsValue = JsString(obj.toString)

    override def read(json: JsValue): T#Value = {
      json match {
        case JsString(txt) => enu.withName(txt)
        case somethingElse => throw DeserializationException(s"Expected a value from enum $enu instead of $somethingElse")
      }
    }
  }

  implicit val enumConverter = new EnumJsonConverter(DeviceType)

  implicit val iotMessageFormat: RootJsonFormat[IOTMessage] = jsonFormat3(IOTMessage.apply)

  implicit val lectureFormat: RootJsonFormat[Lecture] = jsonFormat4(Lecture.apply)

  implicit val sendMessageFormat: RootJsonFormat[SendMessage] = jsonFormat3(SendMessage)
  implicit val addDeviceFormat: RootJsonFormat[AddDevice] = jsonFormat3(AddDevice)
  implicit val receivemessageFormat: RootJsonFormat[ReceiveMessage] = jsonFormat3(ReceiveMessage.apply)

  implicit val registerFormat: RootJsonFormat[Register] = jsonFormat1(Register)
  implicit val notifyFormat: RootJsonFormat[Notify] = jsonFormat1(Notify)

  implicit val deviceFormat: RootJsonFormat[Device] = jsonFormat5(Device)
  implicit val deviceSeqFormat: RootJsonFormat[Seq[Device]] = immSeqFormat(deviceFormat)

  implicit val sensorForemat: RootJsonFormat[Sensor] = jsonFormat6(Sensor)
  implicit val sensorsSeqFormat: RootJsonFormat[Seq[Sensor]] = immSeqFormat(sensorForemat)
  implicit val deviceSensorsImmSeqFormat: RootJsonFormat[scala.collection.Seq[Sensor]] = seqFormat(sensorForemat)

  implicit val deviceSensorFormat: RootJsonFormat[DeviceSensors] = jsonFormat2(DeviceSensors)
  implicit val deviceSensorsSeqFormat: RootJsonFormat[Seq[DeviceSensors]] = immSeqFormat(deviceSensorFormat)

  implicit val userFormat: RootJsonFormat[User] = jsonFormat3(User)

}
