package br.com.diegosilva.home.dto


object IOTMessage {

  def decode(message: String): Option[IOTMessage] = {
    val map: Map[String, String] = message.split(",")
      .map(value => value.split(":")(0) -> value.split(":")(1)).toMap


    val retorno = IOTMessage()

    if (map.contains("id"))
      retorno.id = map.getOrElse("id", "")
    else
      return None

    if (map.contains("sen"))
      retorno.sensor = map.getOrElse("sen", "")
    else
      return None

    if (map.contains("val"))
      retorno.value = map.getOrElse("val", "")
    else
      return None

    Some(retorno)
  }
}


case class IOTMessage(var id: String = "", var sensor: String = "", var value: String = "") {

  def encode: String = "id:" + this.id + "," + "sen:" + this.sensor + "," + "val:" + value

  override def toString: String = "IOTMessage{" + "id='" + id + '\'' + ", sensor='" + sensor + '\'' + ", value='" + value + '\'' + '}'
}
