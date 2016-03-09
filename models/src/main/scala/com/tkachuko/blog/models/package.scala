package com.tkachuko.blog

package object models {

  case class Post(id: Long = System.currentTimeMillis(), title: String, content: String)
}
