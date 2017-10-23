package com.tkachuko.blog.frontend.controllers

trait AsyncLoader {

  val token: String = "lszijr65VV8oxrkkwPIolGFG0zpgpZhW"

  type Id

  type Data

  type Callback = List[Data] => Unit

  type StringCallback = String => Unit

  def loadAll(callback: Callback): Unit

  def loadOne(id: Id)(callback: Callback): Unit
}
