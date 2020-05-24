package br.com.diegosilva.home.domain

import scala.concurrent.duration.Duration

case class Trigger(toUid: String, activateValue: Any, setValue: Any, duration: Duration)
case class Sensor(uid: String, triggers: Seq[Trigger])
