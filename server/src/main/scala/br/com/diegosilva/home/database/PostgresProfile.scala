package br.com.diegosilva.home.database

import br.com.diegosilva.home.data.DeviceType
import com.github.tminglei.slickpg._
import slick.basic.Capability

import scala.concurrent.duration.Duration



trait PostgresProfile extends ExPostgresProfile
  with PgArraySupport
  with PgDate2Support
  with PgRangeSupport
  with PgHStoreSupport
  with PgSearchSupport
  with PgNetSupport
  with PgLTreeSupport {
  def pgjson = "jsonb"

  override protected def computeCapabilities: Set[Capability] =
    super.computeCapabilities + slick.jdbc.JdbcCapabilities.insertOrUpdate

  override val api = MyAPI

  object MyAPI extends API with ArrayImplicits
    with DateTimeImplicits
//    with JsonImplicits
    with NetImplicits
    with LTreeImplicits
    with RangeImplicits
    with HStoreImplicits
    with SearchImplicits
    with SearchAssistants {
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)

    implicit val scDurationTypeMapper = MappedColumnType.base[Duration, String](
      { dur => dur.toString },
      { str => Duration(str) }
    )

    implicit val anyTypeMapper = MappedColumnType.base[Any, String](
      { dur => dur.toString },
      { str => str }
    )

    implicit val deviceType = MappedColumnType.base[DeviceType.Value, String](
      { value => value.toString },
      { str => DeviceType.withName(str) }
    )
  }
}

object PostgresProfile extends PostgresProfile
