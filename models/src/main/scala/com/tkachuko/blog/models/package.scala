package com.tkachuko.blog

package object models {

  type Content = String
  type Title = String
  type Tags = List[String]
  type Millis = Double

  case class Post(title: Title,
                  content: Content,
                  tags: Tags = List.empty,
                  created: Millis = System.currentTimeMillis())

  case class PostInfo(title: Title, tags: Tags, created: Millis)

}
