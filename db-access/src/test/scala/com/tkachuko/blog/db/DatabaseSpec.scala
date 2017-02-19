package com.tkachuko.blog.db

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.tkachuko.blog.db.internal.Database
import com.tkachuko.blog.models.Post
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class DatabaseSpec extends TestKit(ActorSystem("DatabaseActorSpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  val `posts repository` = new Database.Posts()
  val `info repository` = new Database.PostsInfo()

  "Repository implementation" should {

    "retrieve all posts info in chronological order" in {
      `info repository`.query.await
        .map(_.title) shouldBe List("Title5", "Title4", "Title3", "Title2", "Title1")
    }

    "retrieve post by title" in {
      `posts repository`.query("Title3").await should not be empty
    }

    "find all post infos by tags in chronological order" in {
      `info repository`.query(List("scala", "akka")).await
        .map(_.title) shouldBe List("Title5", "Title4", "Title3")
    }

    "provide count of posts" in {
      `posts repository`.count.await shouldBe 5
    }

  }

  override protected def beforeAll(): Unit = {
    InMemoryMongo.start()
    val repository = new Database.Posts()
    repository.insert(Post("Title1", "Hello!")).await
    repository.insert(Post("Title2", "Hello!")).await
    repository.insert(Post("Title3", "Hello!", List("akka", "scala"))).await
    repository.insert(Post("Title4", "Hello!", List("akka"))).await
    repository.insert(Post("Title5", "Hello!", List("scala"))).await
  }

  override def afterAll(): Unit = {
    InMemoryMongo.stop()
    TestKit.shutdownActorSystem(system)
  }

  implicit class Awaitable[T](future: Future[T]) {

    def await: T = Await.result(future, 10 seconds)
  }

}