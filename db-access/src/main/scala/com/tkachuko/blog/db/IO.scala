package com.tkachuko.blog.db

import com.tkachuko.blog.models.{Post, Subscription}
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, Macros}

object IO {

  implicit val postBsonReader: BSONDocumentReader[Post] = Macros.reader[Post]
  implicit val postBsonWriter: BSONDocumentWriter[Post] = Macros.writer[Post]

  implicit val subscriptionBsonReader: BSONDocumentReader[Subscription] = Macros.reader[Subscription]
  implicit val subscriptionBsonWriter: BSONDocumentWriter[Subscription] = Macros.writer[Subscription]

  object Names {

    val posts = "posts"

    val subscriptions = "subscriptions"
  }
}
