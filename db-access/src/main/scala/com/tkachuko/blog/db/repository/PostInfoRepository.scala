package com.tkachuko.blog.db.repository

import com.tkachuko.blog.models.{PostInfo, Tags}

import scala.concurrent.Future

/**
  * Repository for all post info operations
  */
trait PostInfoRepository {

  def query: Future[List[PostInfo]]

  def query(tags: Tags): Future[List[PostInfo]]
}
