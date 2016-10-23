package com.tkachuko.blog.db.repository

import com.tkachuko.blog.models.{Post, Tags, Title}

import scala.concurrent.Future

/**
  * According to DDD concept this trait is representation of Post entity specific repository
  */
trait PostRepository {

  def query: Future[List[Post]]

  def query(tags: Tags): Future[List[Post]]

  def query(title: Title): Future[Option[Post]]

  def insert(post: Post): Future[_]

  def count: Future[Int]
}
