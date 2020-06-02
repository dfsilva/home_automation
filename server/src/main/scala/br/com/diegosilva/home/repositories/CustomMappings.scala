package br.com.diegosilva.home.repositories

import scala.concurrent.duration.Duration
import slick.jdbc.PostgresProfile.api._

object CustomMappings {

  implicit val durationTypeMapper = MappedColumnType.base[Duration, String](
    { dur => dur.toString },
    { str => Duration(str) }
  )

  implicit val anyTypeMapper = MappedColumnType.base[Any, String](
    { dur => dur.toString },
    { str => str }
  )
}
