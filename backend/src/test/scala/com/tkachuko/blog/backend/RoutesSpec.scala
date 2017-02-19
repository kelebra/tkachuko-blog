package com.tkachuko.blog.backend

import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.tkachuko.blog.backend.rest.RestService.routes
import com.tkachuko.blog.backend.static.StaticDataResolver._
import com.tkachuko.blog.db.repository.{PostInfoRepository, PostRepository}
import com.tkachuko.blog.models.{PostInfo, Post => BlogPost}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Future

class RoutesSpec extends WordSpecLike with MockFactory with Matchers with ScalatestRouteTest with BeforeAndAfterAll {

  "Web server" should {

    "return homepage for GET request to the root path" in {
      Get() ~> routes(???, ???) ~> check {
        status === StatusCodes.Success
        responseAs[String] should not be empty
      }
    }

    "return static resource for GET request to the /pages/css/index.css" in {
      Get(s"/$resourcePrefix/css/index.css") ~> routes(???, ???) ~> check {
        status === StatusCodes.Success
        responseAs[String] should not be empty
      }
    }

    "return all posts info as json for GET to the /posts/info" in {
      val infos = List(PostInfo("post", List.empty, System.currentTimeMillis()))
      val repository = mock[PostInfoRepository]
      (repository.query _).expects().returning(infos.success)

      Get(s"/$posts/info") ~> routes(???, repository) ~> check {
        status === StatusCodes.Success
        responseAs[String] should not be empty
      }
    }

    "return single post as json for GET to the /post/id" in {
      val post = BlogPost("hello", "hello guys", List("akka", "scala"))
      val repository = mock[PostRepository]
      (repository.query _).expects("hello").returning(Option(post).success)


      Get(s"/$postByTitle/hello") ~> routes(repository, ???) ~> check {
        status === StatusCodes.Success
        responseAs[String] should not be empty
      }
    }

    "return post infos that contain at least one specified tags via /tags" in {
      val infos = List(PostInfo("post", List("akka", "scala"), System.currentTimeMillis()))
      val repository = mock[PostInfoRepository]
      (repository.query(_: List[String])).expects(*).returning(infos.success)

      Post(s"/$postsByTags", HttpEntity("akka, scala")) ~> routes(???, repository) ~> check {
        status === StatusCodes.Success
        responseAs[String] should not be empty
      }
    }
  }

  implicit class SuccessfulFuture[T](any: T) {

    def success: Future[T] = Future.successful(any)
  }

}
