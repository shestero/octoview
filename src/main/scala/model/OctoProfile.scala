package model

import io.circe.Codec
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

import java.time.Instant
import io.circe.generic.extras.auto.*
import io.circe.generic.extras.semiauto.*

case class ConnectionData(
                           ip: String,
                           country: String
                         )

case class OctoProfile(
                        uuid: String,
                        state: String, // "STARTED"
                        headless: Option[Boolean], // ?
                        startTime: Int,
                        wsEndpoint: String, // like "ws://127.0.0.1:57709/devtools/browser/...",
                        debugPort: Int, // comes as String
                        oneTime: Boolean,
                        browserPID: Int,
                        connectionData: ConnectionData
                      )
{
  lazy val  startTimeInstant: Instant = Instant.ofEpochSecond(startTime.toLong)

  def print(): Unit = {
    println(s"\t$uuid ($state)" + (if headless.contains(true) then "" else " NOT headless!?"))
    println(s"\t$wsEndpoint")
    println(s"\tstarted at\t: $startTimeInstant")
    println(s"\tIP:\t${connectionData.ip} (${connectionData.country})")
    println
  }
}


object OctoProfile:
  given Configuration = Configuration.default.withSnakeCaseMemberNames
  given Codec[OctoProfile] = deriveConfiguredCodec[OctoProfile]

