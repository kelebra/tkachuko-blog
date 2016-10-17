package com.tkachuko.blog.db

import com.tkachuko.blog.models.Post
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, Macros}

object IO {

  implicit val postBsonReader: BSONDocumentReader[Post] = Macros.reader[Post]
  implicit val postBsonWriter: BSONDocumentWriter[Post] = Macros.writer[Post]

  object Names {

    val posts = "posts"

    val subscriptions = "subscriptions"
  }
}
