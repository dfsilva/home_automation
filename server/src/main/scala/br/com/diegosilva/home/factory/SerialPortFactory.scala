package br.com.diegosilva.home.factory

import br.com.diegosilva.home.serial.{Rf24SerialInterface, RxTxSerialInterface, SerialInterface}
import com.typesafe.config.Config

object SerialType extends Enumeration {
  val RXTX = Value("rxtx")
  val RF24 = Value("rf24")
}

object SerialPortFactory {

  private var _instance: SerialInterface = null

  def get(config: Config): SerialInterface = {
    if (_instance == null) {
      val interface = config.getEnum(SerialType.getClass, "serial.interface")
      interface match {
        case SerialType.RXTX => {
          _instance = new RxTxSerialInterface(portName = config.getString("serial.port"))
        }
        case SerialType.RF24 => {
          _instance = new Rf24SerialInterface(config.getInt("rfnetwork.node"))
        }
      }
    }
    _instance
  }

}
