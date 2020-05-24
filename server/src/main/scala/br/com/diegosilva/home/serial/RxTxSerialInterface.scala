package br.com.diegosilva.home.serial

import jssc.SerialPort

class RxTxSerialInterface(portName: String) extends SerialInterface {

  private val serialPort: jssc.SerialPort = new SerialPort(portName)
  serialPort.openPort()
  serialPort.setParams(jssc.SerialPort.BAUDRATE_57600, jssc.SerialPort.DATABITS_8, jssc.SerialPort.STOPBITS_1, jssc.SerialPort.PARITY_NONE)
  val mask = jssc.SerialPort.MASK_RXCHAR + jssc.SerialPort.MASK_CTS + jssc.SerialPort.MASK_DSR
  serialPort.setEventsMask(mask)

  override def send(node: Int, value: String): Boolean = {
    serialPort.writeString(value)
    true
  }

  override def onReceive(listener: String => Unit): Unit = {
    val message = new StringBuilder()
    serialPort.addEventListener { event =>
      if (event.isRXCHAR && event.getEventValue > 0) {
        val buffer: Array[Byte] = serialPort.readBytes()
        buffer.foreach { b =>
          if ((b == '\n') && message.length > 0) {
            listener(message.toString.replaceAll("[^a-zA-Z0-9:,;._]", ""))
            message.clear()
            message.setLength(0)
          }
          else message.append(b.toChar)
        }
      }
    }
  }
}
