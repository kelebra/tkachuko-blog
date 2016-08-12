package com.tkachuko.blog.frontend.controllers

trait AsyncLoader {

  type Id

  type Data

  type Callback = List[Data] => Unit

  def loadAll(callback: Callback): Unit

  def loadOne(id: Id)(callback: Callback): Unit
}
