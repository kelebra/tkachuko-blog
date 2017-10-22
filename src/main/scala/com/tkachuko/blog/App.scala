package com.tkachuko.blog

import akka.actor.{ActorSystem, Props}
import com.tkachuko.blog.service.url.UrlActor
import org.scalajs.dom._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

object App extends js.JSApp {

  @JSExport
  def main(): Unit = {
    val system = ActorSystem("tkachuko-blog")
    val url = system.actorOf(Props(UrlActor))
    url ! ""
    system.log.info("Started router: {}", url)
    system.registerOnTermination(system.log.info("Shutting down"))
  }
}
