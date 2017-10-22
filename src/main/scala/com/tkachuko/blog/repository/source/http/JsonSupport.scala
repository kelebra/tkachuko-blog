package com.tkachuko.blog.repository.source.http

import com.tkachuko.blog.model.{Infos, Post, PostInfo, Posts}
import play.api.libs.json.{JsResult, Json, Reads}

import scala.language.implicitConversions

object JsonSupport {

  type Unmarshal[T] = String => T

  private implicit val postsFormat: Reads[Post] = Json.reads[Post]
  private implicit val infosFormat: Reads[PostInfo] = Json.reads[PostInfo]

  implicit def flatTry[T](tr: JsResult[T]): T = tr.get

  implicit val postUnmarshal: Unmarshal[Posts] = json => Json.fromJson[Posts](Json.parse(json))

  implicit val infoUnmarshal: Unmarshal[Infos] = json => Json.fromJson[Infos](Json.parse(json))
}
