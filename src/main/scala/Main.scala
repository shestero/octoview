import com.microsoft.playwright.*
import model.{Login, OctoAutomationProfiles, OctoProfile}
import java.net.http.HttpClient
import java.net.http.HttpClient.Version
import sttp.client4.{UriContext, asString, basicRequest}
import sttp.client4.httpclient.HttpClientSyncBackend
import sttp.client4.circe.*
import io.circe.generic.auto.*
import io.circe.syntax.*

import scala.jdk.CollectionConverters.*
import scala.util.{Try, Using}

object Main extends App {
  println("OctoView:\tHELLO!")
  println

  val loginUrl = s"http://localhost:58890/api/auth/login"
  val apiUrl = s"http://localhost:58890/api/profiles/active"
  val automationUrl = "https://app.octobrowser.net/api/v2/automation/profiles"

  val email = "...@gmail.com"
  val password = "..."
  val payload = Login(email, password)

  // OCTO_API_TOKEN
  val api_token = "..."

  val headers = Map(
    "X-Octo-Api-Token" -> api_token
  )

  Using {
    HttpClientSyncBackend.usingClient(
      HttpClient.newBuilder().version(Version.HTTP_1_1).build()
    )
  } { backendHTTP_1_1 =>
    val pageLen = 100
    def page(page: Int): Unit = {
      println(s"Loading page $page ...")
      val params = Map(
        "page" -> page.toString,
        "page_len" -> pageLen.toString,
        "fields" -> "created_at,updated_at,last_active,title,tags,status"
      )
      val request1 =
        basicRequest
          .get(uri"$automationUrl".addParams(params))
          .headers(headers)
          .response(asJson[OctoAutomationProfiles])

      val response1 = request1.send(backendHTTP_1_1)

      response1.body match {
        case Right(reply) =>
          println("Successfully loaded raw response body (as plain text):")
          println("-" * 40)
          reply.print(page * pageLen)
          println("-" * 40)

        case Left(error) =>
          // Left contains the error body as a String if the status code was non-2xx
          println(s"Failed to load profiles. Non-2xx response body: ${error.getMessage}")
      }
    }

    val pages = 0 to 1
    pages.foreach(page)


    def playWright(cdpUrl: String): Unit =
      Using.resource(Playwright.create) { playwright =>
        println(s"Trying to reach $cdpUrl by PlayWright...")
        Try {
          val chromium = playwright.chromium
          Using{ chromium.connectOverCDP(cdpUrl) } { browser =>
            println(s"browser.contexts.size: ${browser.contexts.size}")
            val pages: Option[List[Page]] =
              Option.unless(browser.contexts.isEmpty || browser.contexts.get(0).pages.isEmpty) {
                browser.contexts.get(0).pages.asScala.toList
              }

            pages.fold {
              println("No pages found!")
            } { pages =>
              println(s"Found ${pages.size} pages:")
              pages.foreach { page =>
                println(s"\tTitle\t: ${page.title}")
                println(s"\tURL\t\t: ${page.url}")
                println(s"\tsize\t: ${page.content().length}")
                println("-" * 40)
              }
            }
          }
          System.out.println("Disconnected from the browser.")
        }.failed.foreach { e =>
          System.err.println(e.getMessage) // e.printStackTrace()
        }
      }

    println(s"Sending post request ${payload.asJson.noSpaces} to $loginUrl")

    val requestLogin =
      basicRequest
        .post(uri"$loginUrl")
        .header("Content-Type", "application/json")
        .body(payload.asJson.noSpaces)
        .response(asString)

    val responseLogin = requestLogin.send(backendHTTP_1_1)

    println( responseLogin.body.fold("Login(ok): " + _, "Login(fail): " + _) )

    val requestPages =
      basicRequest
        .get(uri"$apiUrl")
        .response(asJson[Seq[OctoProfile]])

    val responsePages = requestPages.send(backendHTTP_1_1)

    responsePages.body match {
      case Right(profiles) =>
        println(s"Successfully loaded ${profiles.size} profiles:")
        profiles.foreach { p =>
          p.print()
          playWright(p.wsEndpoint.replace("127.0.0.1", "localhost"))
        }

      case Left(error) =>
        println(s"Failed to load profiles. Error: $error")
    }
  }

  println
  println("OctoView:\tBYE!")
}