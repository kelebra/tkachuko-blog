package com.tkachuko.blog.db

import com.tkachuko.blog.models.Post
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class DatabaseSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  "Database" should {

    "retrieve all records" in {
      Database.Posts.all().await should not be empty
    }
  }

  override protected def beforeAll(): Unit = {
    InMemoryMongo.start()
    Database.Posts.insert(Post("Title1", "Hello!")).await
  }

  override def afterAll(): Unit = InMemoryMongo.stop()

  implicit class Awaitable[T](future: Future[T]) {

    def await: T = Await.result(future, 5 seconds)
  }

}