package br.com.diegosilva.home.domain

case class Trigger(toUid: String, value: Any, setValue: Any)

case class Sensor(uid: String, triggers: Seq[Trigger])
