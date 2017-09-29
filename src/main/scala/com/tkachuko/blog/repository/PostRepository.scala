package com.tkachuko.blog.repository

import com.tkachuko.blog.model.{Post, Posts, Title}
import com.tkachuko.blog.repository.read.ReadOnlyRepository

object PostRepository extends ReadOnlyRepository[Post, Posts, Title]{

  def read(id: Title) = ???

  def all = ???
}
