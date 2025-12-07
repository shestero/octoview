package model

import io.circe.generic.auto.*

case class Login(
                  email: String,
                  password: String
                )
