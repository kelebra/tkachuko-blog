package com.tkachuko.blog.json

import com.tkachuko.blog.models.{Post, PostInfo}
import play.api.libs.json.{Json, Reads}

sealed trait JsonRepository[T] {

  implicit val reads: Reads[T]

  def multiple(json: String): Seq[T] =
    Json.fromJson[Seq[T]](Json.parse(json)).getOrElse(Nil)

  def single(json: String): Option[T] = multiple(json).headOption
}

object PostJsonRepository extends JsonRepository[Post] {
  override implicit val reads: Reads[Post] = Json.reads[Post]
}

object PostInfoJsonRepository extends JsonRepository[PostInfo] {
  override implicit val reads: Reads[PostInfo] = Json.reads[PostInfo]
}