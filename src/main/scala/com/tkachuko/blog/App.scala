package com.tkachuko.blog

import akka.actor.ActorSystem
import com.tkachuko.blog.service.url.PersistentUrlActor

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

object App extends js.JSApp {

  @JSExport
  def main(): Unit = {
    val system = ActorSystem("tkachuko-blog")
    val viewRouter = system.actorOf(PersistentUrlActor())

    viewRouter ! "go"
  }
}
