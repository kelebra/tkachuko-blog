package com.tkachuko.blog

import com.tkachuko.blog.db.IO._
import com.tkachuko.blog.models.{Post, Subscription}
import com.typesafe.config.ConfigFactory
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.{MongoConnection, MongoDriver}
import reactivemongo.bson.{BSONArray, BSONDocument}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

package object db {

  val config = ConfigFactory.load("db.conf")
  val name = config.getString("db.name")

  object Database {

    val driver = new MongoDriver
    val uri = config.getString("db.uri")

    object Posts {

      private val queryAll = BSONDocument()
      private val chronologicalOrder = BSONDocument("created" -> -1)

      val collection: Future[BSONCollection] = connection.map(_ (Names.posts))

      def all(): Future[List[Post]] = collection.flatMap(
        _.find(queryAll).sort(chronologicalOrder).cursor[Post]().collect[List]()
      )

      def count(): Future[Int] = collection.flatMap(_.count())

      def insert(post: Post): Future[WriteResult] = collection.flatMap(_.insert(post))

      def findByTitle(title: String): Future[Option[Post]] = collection.flatMap(
        _.find(BSONDocument("title" -> title)).one[Post]
      )

      def findByTags(tags: List[String]): Future[List[Post]] = {
        val conditions = BSONArray(tags.map(tag => BSONDocument("tags" -> tag)))
        collection.flatMap(
          _.find(BSONDocument("$or" -> conditions)).sort(chronologicalOrder).cursor[Post]().collect[List]()
        )
      }
    }

    object Subscriptions {

      val collection: Future[BSONCollection] = connection.map(_ (Names.subscriptions))

      def count(): Future[Int] = collection.flatMap(_.count())

      def insert(subscription: Subscription): Future[WriteResult] = collection.flatMap(_.insert(subscription))
    }

    lazy val connection = MongoConnection.parseURI(uri) match {
      case Success(parsedUri) => driver.connection(parsedUri).database(name)
      case _ => throw new IllegalArgumentException(s"Invalid mongo URI specifier: $uri")
    }
  }

}
