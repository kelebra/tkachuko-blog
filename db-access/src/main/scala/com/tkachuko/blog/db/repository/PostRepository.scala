package com.tkachuko.blog.db.repository

import com.tkachuko.blog.models.{Post, Title}

import scala.concurrent.Future

/**
  * This trait is representation of Post entity specific repository
  */
trait PostRepository {

  def query(title: Title): Future[Option[Post]]

  def insert(post: Post): Future[_]

  def count: Future[Int]
}
