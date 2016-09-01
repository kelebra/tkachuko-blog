package com.tkachuko.blog.backend

import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.tkachuko.blog.backend.WebServer.RestService.routes
import com.tkachuko.blog.backend.static.StaticDataResolver._
import com.tkachuko.blog.db.{Database, InMemoryMongo}
import com.tkachuko.blog.models.{Post => BlogPost}
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

    "return single post as json for GET to the /post/id" in {
      Get(s"/$postByTitle/hello") ~> routes ~> check {
        status === StatusCodes.Success
        responseAs[String] should not be empty
      }
    }

    "return true or false after subscription via the /subscribe" in {
      Get(s"/$subscribe/email") ~> routes ~> check {
        status === StatusCodes.Success
        responseAs[String] shouldBe "true"
      }
    }

    "return posts that contain at least one specified tags via /tags" in {
      Post(s"/$postsByTags", HttpEntity("akka, scala")) ~> routes ~> check {
        status === StatusCodes.Success
        responseAs[String] should not be empty
      }
    }
  }

  override protected def beforeAll(): Unit = {
    InMemoryMongo.start()
    Database.Posts.insert(BlogPost("hello", "hi"))
    Database.Posts.insert(BlogPost("hello1", "hi", List("akka", "scala")))
    Database.Posts.insert(BlogPost("hello2", "hi", List("scala")))
    Database.Posts.insert(BlogPost("hello3", "hi", List("akka")))
  }

  override protected def afterAll(): Unit = {
    InMemoryMongo.stop()
  }
}
