package com.tkachuko.blog.repository.source

import akka.actor.{Actor, ActorLogging, Props}

package object memory {

  object Read

  class InMemorySource[T](data: T) extends Actor with ActorLogging {

    def receive: Receive = {
      case Read => sender() ! data
    }
  }

  object InMemorySource {

    def apply[T](data: T): Props = Props(classOf[InMemorySource[T]], data)
  }

}
