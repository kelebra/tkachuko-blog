package com.tkachuko.blog.view

import akka.actor.Props
import com.tkachuko.blog.model.Title

class Post(title: Title) extends ViewActor {

  def view = ???
}

object Post {

  def apply(title: Title): Props = Props(classOf[Post], title)
}
