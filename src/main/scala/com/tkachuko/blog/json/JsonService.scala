package com.tkachuko.blog.json

trait JsonService[T] {

  val json: JsonRepository[T]
}