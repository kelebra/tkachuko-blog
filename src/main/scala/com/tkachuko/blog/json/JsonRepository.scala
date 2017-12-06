package com.tkachuko.blog.json

import com.tkachuko.blog.models.{Post, PostInfo}
import play.api.libs.json.{Json, Reads}

trait JsonRepository[T] {

  implicit val reads: Reads[T]

  def fromJson(json: String): List[T] = Json.fromJson[List[T]](Json.parse(json)).getOrElse(Nil)
}

object JsonRepository {

  val posts: JsonRepository[Post] = new JsonRepository[Post] {
    override implicit val reads: Reads[Post] = Json.reads[Post]
  }

  val postInfo: JsonRepository[PostInfo] = new JsonRepository[PostInfo] {
    override implicit val reads: Reads[PostInfo] = Json.reads[PostInfo]
  }
}