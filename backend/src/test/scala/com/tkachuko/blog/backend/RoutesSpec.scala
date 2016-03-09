package com.tkachuko.blog.backend

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.tkachuko.blog.backend.WebServer.routes
import com.tkachuko.blog.backend.static.StaticDataResolver._
import com.tkachuko.blog.db.Database
import com.tkachuko.blog.models.{Post => BlogPost}
import org.h2.tools.Server
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
      Get(s"/$resourcePrefix/css/index.css") ~> routes ~> check {
        status === StatusCodes.Success
        responseAs[String] should not be empty
      }
    }

    "return all posts as json for GET to the /posts" in {
      Get(s"/$posts") ~> routes ~> check {
        status === StatusCodes.Success
        responseAs[String] should not be empty
      }
    }
  }

  override protected def beforeAll(): Unit = {
    Server.createTcpServer("-tcpAllowOthers").start()
    Database.initialize()
    Database.save(BlogPost(1, "title", "content"))
    Database.save(BlogPost(2, "title other", "content"))
  }
}
