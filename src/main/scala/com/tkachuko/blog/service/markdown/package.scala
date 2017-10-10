package com.tkachuko.blog.service

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import com.tkachuko.blog.service.markdown.MarkdownActor.Render

import scala.language.postfixOps

package object markdown {

  class MarkdownActor extends Actor {

    import context.dispatcher

    def receive: Receive = {
      case Render(input) => self forward input
      case text: String  => pipe(Algorithm(text)) to sender()
    }
  }

  object MarkdownActor {

    sealed case class Render(input: String)

    def apply(): Props = Props(classOf[MarkdownActor])
  }

}
