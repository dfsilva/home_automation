package br.com.diegosilva.home.serial

import br.com.diegosilva.rfnative.RfNative

class Rf24SerialInterface(node: Int) extends SerialInterface {

  private var rfNative: RfNative = null

  override def onReceive(listener: String => Unit): Unit = {
    if (rfNative == null) {
      new RfNative((instance, msg) => {
        rfNative = instance
        listener(msg)
      }).start(node);
    }
  }

  override def send(node: Int, value: String): Boolean = {
    rfNative.send(node, value);
  }
}
