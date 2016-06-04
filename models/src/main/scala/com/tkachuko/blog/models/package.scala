package com.tkachuko.blog

package object models {

  case class Post(title: String, content: String, created: Long = System.currentTimeMillis())
}
