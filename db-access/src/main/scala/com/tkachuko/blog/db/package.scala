package com.tkachuko.blog

import com.tkachuko.blog.models.Post
import com.typesafe.config.ConfigFactory
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.{MongoConnection, MongoDriver}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, Macros}

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

      val collection: Future[BSONCollection] = connection.map(_ ("posts"))

      implicit val reader: BSONDocumentReader[Post] = Macros.reader[Post]
      implicit val writer: BSONDocumentWriter[Post] = Macros.writer[Post]

      def all(): Future[List[Post]] =
        collection.flatMap(_.find(BSONDocument()).cursor[Post]().collect[List]())

      def insert(post: Post): Future[WriteResult] = collection.flatMap(_.insert(post))

      def findByTitle(title: String): Future[Option[Post]] =
        collection.flatMap(_.find(BSONDocument("title" -> title)).one[Post])
    }

    lazy val connection = MongoConnection.parseURI(uri) match {
      case Success(parsedUri) => driver.connection(parsedUri).database(name)
      case _ => throw new IllegalArgumentException(s"Invalid mongo URI specifier: $uri")
    }
  }

}
