package br.com.diegosilva.home.factory

import br.com.diegosilva.home.serial.{RxTxSerialInterface, SerialInterface}
import com.typesafe.config.Config

object SerialPortFactory {

  private var _instance: SerialInterface = null

  def get(config: Config): SerialInterface = {
    if (_instance == null) {
      val interface = config.getString("serial.interface")
      interface match {
        case "rxtx" => {
          _instance = new RxTxSerialInterface(portName = config.getString("serial.port"))
        }
        case "rf24" => {

        }
      }
    }
    _instance
  }

}
