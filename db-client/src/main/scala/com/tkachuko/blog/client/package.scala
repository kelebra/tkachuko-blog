package com.tkachuko.blog

import java.util.UUID

import com.tkachuko.blog.models.Post

import scala.util.{Failure, Try}

package object client {

  trait Namespace

  case class Reply[T](namespace: Namespace, correlation: String, data: Try[T])

  abstract class Request(val namespace: Namespace, val id: String = UUID.randomUUID().toString) {

    def success[T](data: T) = Reply(namespace, id, Try(data))

    def failure(msg: String) = Reply(namespace, id, Failure(new RuntimeException(msg)))
  }

  /**
    * Requests that are under Posts namespace
    */

  case object Posts extends Namespace

  case class Insert(post: Post) extends Request(Posts)

  case class FindByTitle(title: String) extends Request(Posts)

  case class Count() extends Request(Posts)

  /**
    * Requests that are under PostInfo namespace
    */

  case object PostsDescriptions extends Namespace

  case class All() extends Request(PostsDescriptions)

  case class FindByTags(tags: List[String]) extends Request(PostsDescriptions)

}
