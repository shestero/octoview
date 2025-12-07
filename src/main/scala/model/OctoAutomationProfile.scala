package model

import io.circe.{Codec, Decoder}

import java.time.Instant
import java.util.UUID
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.*

case class OctoAutomationProfile(
                                  uuid: String, // UUID,
                                  title: String,
                                  tags: List[String],
                                  status: Int,
                                  createdAt: Instant,
                                  lastActive: Option[Instant],
                                  updatedAt: Option[Instant]
                                ) {
  infix def printHeader(num: Int): Unit = {
    println(s"\t#\t\t: $num")
    println(s"\tuuid\t: $uuid")
    println(s"\ttitle\t: $title")
    println(s"\ttags\t: ${tags.sorted.map("[" + _ + "]").mkString("")}")
    println(s"\tstatus\t: $status")
    println(s"\tcreatedAt\t: $createdAt")
    println(s"\tlastActive\t: $lastActive")
    println(s"\tupdatedAt\t: $updatedAt")
    println
  }
}

object OctoAutomationProfile:
  given Configuration = Configuration.default.withSnakeCaseMemberNames

  import model.decoderInstant

  given Codec[OctoAutomationProfile] = deriveConfiguredCodec[OctoAutomationProfile]