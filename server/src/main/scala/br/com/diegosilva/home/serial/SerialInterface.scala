package br.com.diegosilva.home.serial

trait SerialInterface {
  def onReceive(listener: (String) => Unit): Unit
  def send(node: Int, value: String): Boolean
}
