package com.tkachuko.blog.view

import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout
import org.scalajs.dom.html.Div
import org.scalajs.dom.{Node, document}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scalatags.JsDom

case object Render

trait ViewActor extends Actor with ActorLogging {

  type DivView = Future[JsDom.TypedTag[Div]]

  implicit val timeout: Timeout = 5 seconds

  import context.dispatcher

  def receive: Receive = {
    case Render => view.map(_.render).foreach(node => replaceBodyWith(node))
  }

  protected def view: DivView

  private def replaceBodyWith(elements: Node*): Unit = {
    document.body.innerHTML = ""
    elements.foreach(document.body.appendChild)
  }
}
