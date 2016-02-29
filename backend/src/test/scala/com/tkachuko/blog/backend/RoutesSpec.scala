package com.tkachuko.blog.backend

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.tkachuko.blog.backend.WebServer.routes
import org.scalatest.{Matchers, WordSpec}

class RoutesSpec extends WordSpec with Matchers with ScalatestRouteTest {

  "Web server" should {

    "return homepage for GET request to the root path" in {
      Get() ~> routes ~> check {
        status === StatusCodes.Success
        responseAs[String] should not be empty
      }
    }

    "return static resource for GET request to the /pages/css/index.css" in {
      Get("/pages/css/index.css") ~> routes ~> check {
        status === StatusCodes.Success
        responseAs[String] should not be empty
      }
    }
  }
}
