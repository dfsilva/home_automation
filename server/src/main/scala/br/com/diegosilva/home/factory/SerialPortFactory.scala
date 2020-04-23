package br.com.diegosilva.home.factory

import jssc.SerialPort

object SerialPortFactory {

  private var _instance: jssc.SerialPort = null

  def get(portName: String): SerialPort = {
    if (_instance == null) {
      _instance = new SerialPort(portName)
      _instance.openPort()
      _instance.setParams(jssc.SerialPort.BAUDRATE_57600, jssc.SerialPort.DATABITS_8, jssc.SerialPort.STOPBITS_1, jssc.SerialPort.PARITY_NONE)
      val mask = jssc.SerialPort.MASK_RXCHAR + jssc.SerialPort.MASK_CTS + jssc.SerialPort.MASK_DSR
      _instance.setEventsMask(mask)
      return _instance
    }
    _instance
  }

}
