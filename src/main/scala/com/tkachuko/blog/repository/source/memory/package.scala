package com.tkachuko.blog.repository.source

import akka.actor.{Actor, ActorLogging}

package object memory {

  class InMemorySource extends Actor with ActorLogging {

    def receive: Receive = {
      case _ => ???
    }
  }

}
