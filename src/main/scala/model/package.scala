import io.circe.Decoder

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

package object model {

  private val formatter: DateTimeFormatter =
    DateTimeFormatter
      .ofPattern("yyyy-MM-dd'T'HH:mm:ss")
      .withZone(java.time.ZoneOffset.UTC) // Assume UTC if no zone is provided

  given decoderInstant: Decoder[Instant] = Decoder.decodeString.emap { str =>
    try {
      val temporal: TemporalAccessor = formatter.parse(str)
      val instant = Instant.from(temporal)
      Right(instant)
    } catch {
      case e: Exception => Left(s"Text '$str' could not be parsed as Instant: ${e.getMessage}")
    }
  }
}