package com.tkachuko.blog

package object models {

  case class Post(title: String,
                  content: String,
                  tags: List[String] = List.empty,
                  created: Long = System.currentTimeMillis())

  case class Subscription(email: String)

}
