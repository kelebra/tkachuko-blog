package com.tkachuko.blog.db

import com.tkachuko.blog.db.repository.{PostInfoRepository, PostRepository}
import com.tkachuko.blog.models._
import com.typesafe.config.ConfigFactory
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.{MongoConnection, MongoDriver}
import reactivemongo.bson.{BSONArray, BSONDocument, BSONDocumentReader, BSONDocumentWriter, Macros}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

package object internal {

  object Database extends MongoConnectivity {

    class Posts(collection: => Future[BSONCollection] = posts)
      extends PostRepository with BsonFormatSupport {

      def query(title: Title): Future[Option[Post]] = collection.flatMap(
        _.find(BSONDocument("title" -> title)).one[Post]
      )

      def count: Future[Int] = collection.flatMap(_.count())

      def insert(post: Post): Future[WriteResult] = collection.flatMap(_.insert(post))
    }

    class PostsDescription(collection: => Future[BSONCollection] = posts)
      extends PostInfoRepository with BsonFormatSupport {

      def query(tags: Tags): Future[List[PostInfo]] = {
        val conditions = BSONArray(tags.map(tag => BSONDocument("tags" -> tag)))
        collection.flatMap(
          _.find(BSONDocument("$or" -> conditions)).sort(chronologicalOrder).cursor[PostInfo]().collect[List]()
        )
      }

      def query: Future[List[PostInfo]] = collection.flatMap(
        _.find(queryAll).sort(chronologicalOrder).cursor[PostInfo]().collect[List]()
      )
    }

  }

  trait BsonFormatSupport {

    implicit val postBsonReader: BSONDocumentReader[Post] = Macros.reader[Post]
    implicit val postBsonWriter: BSONDocumentWriter[Post] = Macros.writer[Post]

    implicit val postInfoBsonReader: BSONDocumentReader[PostInfo] = Macros.reader[PostInfo]
    implicit val postInfoBsonWriter: BSONDocumentWriter[PostInfo] = Macros.writer[PostInfo]
  }

  trait MongoConnectivity {

    private val config = ConfigFactory.load("db.conf")
    private val (name, uri) = (config.getString("db.name"), config.getString("db.uri"))

    lazy val connection = MongoConnection.parseURI(uri) match {
      case Success(parsedUri) => new MongoDriver().connection(parsedUri).database(name)
      case _ => throw new IllegalArgumentException(s"Invalid mongo URI specified: $uri")
    }

    lazy val posts = connection.map(_ (Names.posts))

    val queryAll = BSONDocument()
    val chronologicalOrder = BSONDocument("created" -> -1)
  }

  private object Names {

    val posts = "posts"
  }

}
