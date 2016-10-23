package com.tkachuko.blog.db

import com.tkachuko.blog.db.repository.PostRepository
import com.tkachuko.blog.models.{Post, Tags, Title}
import com.typesafe.config.ConfigFactory
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.{MongoConnection, MongoDriver}
import reactivemongo.bson.{BSONArray, BSONDocument, BSONDocumentReader, BSONDocumentWriter, Macros}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

package object internal {

  val config = ConfigFactory.load("db.conf")
  val name = config.getString("db.name")

  object Database extends MongoConnectivity {

    class Posts(collection: => Future[BSONCollection] = posts)
      extends PostRepository {

      import IO._

      def query: Future[List[Post]] = collection.flatMap(
        _.find(queryAll).sort(chronologicalOrder).cursor[Post]().collect[List]()
      )

      def query(title: Title): Future[Option[Post]] = collection.flatMap(
        _.find(BSONDocument("title" -> title)).one[Post]
      )

      def query(tags: Tags): Future[List[Post]] = {
        val conditions = BSONArray(tags.map(tag => BSONDocument("tags" -> tag)))
        collection.flatMap(
          _.find(BSONDocument("$or" -> conditions)).sort(chronologicalOrder).cursor[Post]().collect[List]()
        )
      }

      def count: Future[Int] = collection.flatMap(_.count())

      def insert(post: Post): Future[WriteResult] = collection.flatMap(_.insert(post))
    }

  }

  trait MongoConnectivity {

    import IO.Names

    private val uri = config.getString("db.uri")

    lazy val connection = MongoConnection.parseURI(uri) match {
      case Success(parsedUri) => new MongoDriver().connection(parsedUri).database(name)
      case _ => throw new IllegalArgumentException(s"Invalid mongo URI specified: $uri")
    }

    lazy val posts = connection.map(_ (Names.posts))

    val queryAll = BSONDocument()
    val chronologicalOrder = BSONDocument("created" -> -1)
  }

  private object IO {

    implicit val postBsonReader: BSONDocumentReader[Post] = Macros.reader[Post]
    implicit val postBsonWriter: BSONDocumentWriter[Post] = Macros.writer[Post]

    object Names {

      val posts = "posts"

      val subscriptions = "subscriptions"
    }

  }

}
