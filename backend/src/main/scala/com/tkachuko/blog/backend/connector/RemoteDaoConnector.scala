package com.tkachuko.blog.backend.connector

import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.pattern.ask
import akka.util.Timeout
import com.tkachuko.blog.client._
import com.tkachuko.blog.db.actor.DbActor
import com.tkachuko.blog.models.Post

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

trait RemoteDaoConnector {

  type Result[T] = Future[T]

  implicit val system: ActorSystem
  implicit val timeout: Timeout = 30 seconds

  val log = system.log

  private def dbActor: ActorRef = system.actorOf(DbActor.props, name = "database-actor")

  def allPosts: Result[List[Post]] = askFor(All(), List.empty[Post])

  def findPostsByTitle(title: String): Result[Option[Post]] = askFor(FindByTitle(title), None)

  def findPostsByTags(tags: List[String]): Result[List[Post]] = askFor(FindByTags(tags), List.empty[Post])

  def postsCount: Result[Int] = askFor(Count(), 0)

  private def askFor[T <: Request, K](request: T, default: K): Result[K] = {
    system.log.info(s"Sending request: $request with id ${request.id}")
    val actor = dbActor
    val result = (actor ? request).dispatch[K](default)
    result.onComplete { case _ => actor ! PoisonPill }
    result
  }

  private implicit class ReplyDispatcher(result: Future[Any]) {

    def dispatch[T](default: T): Future[T] = result
      .map(_.asInstanceOf[Reply[T]])
      .map(
        reply => reply.data match {
          case Success(value) =>
            log.info(s"Reply for id ${reply.correlation} was successful")
            value
          case Failure(exception) =>
            log.error(s"Reply ${reply.correlation} failed. " +
              s"Default reply will be returned. Message: ${exception.getMessage}")
            default
        }
      ).recoverWith { case e => log.error(e, "Could not get reply"); Future.successful(default) }
  }

}
