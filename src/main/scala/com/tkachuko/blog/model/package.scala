package com.tkachuko.blog

package object model {

  type Title = String
  type Timestamp = Double
  type Content = String

  case class Tag(name: Title)

  type Tags = Set[Tag]

  case class Post(title: Title,
                  content: Content,
                  tags: Tags = Set.empty,
                  created: Timestamp = System.currentTimeMillis())

  type Posts = Set[Post]

  sealed trait Info[T]

  case class PostInfo(title: Title, tags: Tags, created: Timestamp) extends Info[Post]

}
