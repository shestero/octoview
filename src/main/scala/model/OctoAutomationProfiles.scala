package model

import io.circe.Codec
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.auto.*
import io.circe.generic.extras.semiauto.*

case class OctoAutomationProfiles(
                                   success: Boolean,
                                   msg: String,
                                   data: List[OctoAutomationProfile],
                                   // code: null
                                   totalCount: Int,
                                   page: Int
                                 ) {
  def print(offset: Int): Unit = {
    println(s"success=$success")
    data.zip(LazyList.from(1 + offset)).foreach(_ printHeader _)
  }
}

object OctoAutomationProfiles:
  given Configuration = Configuration.default.withSnakeCaseMemberNames

  given Codec[OctoAutomationProfiles] = deriveConfiguredCodec[OctoAutomationProfiles]