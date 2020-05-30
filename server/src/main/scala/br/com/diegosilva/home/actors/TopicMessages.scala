package br.com.diegosilva.home.actors

import br.com.diegosilva.home.data.IOTMessage

object TopicMessages {

  final val RF24TOPIC:String = "rf24_topic_message"
  final val RXTXTOPIC:String = "rxtx_topic_message"

  final case class SendTopic(message: IOTMessage)

}
