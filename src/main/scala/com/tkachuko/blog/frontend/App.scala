package com.tkachuko.blog.frontend

import com.tkachuko.blog.frontend.router.Router
import org.scalajs.dom._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

object App extends js.JSApp {

  @JSExport
  def main(): Unit = {
    Router(window.location.hash)
    Router.setupHistoryListener()
  }
}
