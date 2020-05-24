package br.com.diegosilva.home.serial

import br.com.diegosilva.rfnative.RfNative

class Rf24SerialInterface(address: Int) extends SerialInterface {

  private val rfNative: RfNative = new RfNative()
  rfNative.start(address)

  override def onReceive(listener: String => Unit): Unit = {
    rfNative.setReceiListener((_, msg) => {
      listener(msg)
    })
  }

  override def send(node: Int, value: String): Boolean = {
    rfNative.send(node, value);
  }
}
