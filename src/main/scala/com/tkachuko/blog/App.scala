package com.tkachuko.blog

import akka.actor.{ActorSystem, Props}
import com.tkachuko.blog.service.url.IndexEvent
import com.tkachuko.blog.view.ViewRouter

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

object App extends js.JSApp {

  @JSExport
  def main(): Unit = {
    val system = ActorSystem("tkachuko-blog")
    val viewRouter = system.actorOf(Props(ViewRouter))

    viewRouter ! IndexEvent
  }
}
