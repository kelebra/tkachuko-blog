package com.tkachuko.blog.db

import com.tkachuko.blog.models.{Post, Subscription}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class DatabaseSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  "Database" should {

    "retrieve all posts" in {
      Database.Posts.all().await.size shouldBe 5
    }

    "retrieve post with tags" in {
      Database.Posts.findByTitle("Title3").await.getOrElse(
        throw new RuntimeException("Inserted post was not found")
      ).tags should not be empty
    }

    "find posts by tags" in {
      Database.Posts.findByTags(List("scala", "akka")).await.size shouldBe 3
    }

    "retrieve all subscriptions count" in {
      Database.Subscriptions.count().await shouldBe 1
    }

  }

  override protected def beforeAll(): Unit = {
    InMemoryMongo.start()
    Database.Posts.insert(Post("Title1", "Hello!")).await
    Database.Posts.insert(Post("Title2", "Hello!")).await
    Database.Posts.insert(Post("Title3", "Hello!", List("akka", "scala"))).await
    Database.Posts.insert(Post("Title3", "Hello!", List("akka"))).await
    Database.Posts.insert(Post("Title3", "Hello!", List("scala"))).await
    Database.Subscriptions.insert(Subscription("mymail")).await
  }

  override def afterAll(): Unit = InMemoryMongo.stop()

  implicit class Awaitable[T](future: Future[T]) {

    def await: T = Await.result(future, 5 seconds)
  }

}