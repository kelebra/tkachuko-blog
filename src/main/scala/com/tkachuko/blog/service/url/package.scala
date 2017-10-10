package com.tkachuko.blog.service

import akka.actor.{Actor, ActorLogging, Props}

package object url {

  class PersistentUrlActor extends Actor with ActorLogging {

    def receive: Receive = ???
  }

  object PersistentUrlActor {

    def apply: Props = Props(classOf[PersistentUrlActor])
  }

}
