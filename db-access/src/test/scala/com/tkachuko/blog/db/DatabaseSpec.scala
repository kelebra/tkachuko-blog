package com.tkachuko.blog.db

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.tkachuko.blog.client._
import com.tkachuko.blog.db.actor.DbActor
import com.tkachuko.blog.db.internal.Database
import com.tkachuko.blog.models.{Post, PostInfo}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class DatabaseSpec extends TestKit(ActorSystem("DatabaseActorSpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  "Database actor" should {

    val databaseActor = DbActor.local

    "retrieve all posts info in chronological order" in {
      databaseActor ! All()
      val reply = expectMsgClass(classOf[Reply[List[PostInfo]]])
      reply.result.map(_.title) shouldBe List("Title5", "Title4", "Title3", "Title2", "Title1")
    }

    "retrieve post by title" in {
      databaseActor ! FindByTitle("Title3")
      val reply = expectMsgClass(classOf[Reply[Option[Post]]])
      reply.result.getOrElse(
        throw new RuntimeException("Inserted post was not found")
      ).tags should not be empty
    }

    "find all post infos by tags in chronological order" in {
      databaseActor ! FindByTags(List("scala", "akka"))
      val reply = expectMsgClass(classOf[Reply[List[PostInfo]]])
      reply.result.map(_.title) shouldBe List("Title5", "Title4", "Title3")
    }

    "provide count of posts" in {
      databaseActor ! Count()
      val reply = expectMsgClass(classOf[Reply[Int]])
      reply.result shouldBe 5
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

  implicit class TryAssertion[T](reply : Reply[T]) {

    def result: T = reply.data.getOrElse(throw new RuntimeException("Did not get expect value"))
  }

}