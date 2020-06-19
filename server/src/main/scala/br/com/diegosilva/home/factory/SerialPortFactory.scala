package br.com.diegosilva.home.factory

import br.com.diegosilva.home.data.InterfaceType
import br.com.diegosilva.home.serial.{Rf24SerialInterface, RxTxSerialInterface, SerialInterface}
import com.typesafe.config.Config

object SerialPortFactory {

  private var _instance: SerialInterface = null

  def get(config: Config): SerialInterface = {
    if (_instance == null) {
      val interface = InterfaceType.withName(config.getString("serial.interface"))
      interface match {
        case InterfaceType.RXTX => {
          _instance = new RxTxSerialInterface(portName = config.getString("serial.port"))
        }
        case InterfaceType.RF24 => {
          _instance = new Rf24SerialInterface(config.getInt("rfnetwork.node"))
        }
      }
    }
    _instance
  }

}
