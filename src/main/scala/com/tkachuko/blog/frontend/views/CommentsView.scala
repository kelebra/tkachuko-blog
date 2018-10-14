package com.tkachuko.blog.frontend.views

import com.tkachuko.blog.frontend.views.CommentsView.noscript
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.html.{Div, Script}
import scalatags.JsDom.all._

class CommentsView {

  def render(container: Element) = {
    container.appendChild(commentsContainer)
    container.appendChild(loader)
    container.appendChild(setup)
  }

  private def commentsContainer: Div = div(
    id := "remarkbox-div",
    `class` := "item",
    noscript(
      iframe(
        id := "remarkbox-iframe",
        src := "https://my.remarkbox.com/embed?nojs=true",
        style := "height:600px;width:100%;border:none!important",
        tabindex := 0
      )
    )
  ).render

  private def loader: Script =
    script(src := "https://my.remarkbox.com/static/js/iframe-resizer/iframeResizer.min.js").render

  private def setup: Script = script(
    raw(
      """
        |  var rb_owner_key = "a0e80a2a-cf60-11e8-8ee2-040140774501";
        |  var thread_uri = window.location.href;
        |  var thread_fragment = window.location.hash;
        |  function create_remarkbox_iframe() {
        |    var src = "https://my.remarkbox.com/embed?rb_owner_key=" + rb_owner_key + "&thread_uri=" + thread_uri;
        |    var ifrm = document.createElement("iframe");
        |    ifrm.setAttribute("id", "remarkbox-iframe");
        |    ifrm.setAttribute("scrolling", "yes");
        |    ifrm.setAttribute("src", src);
        |    ifrm.setAttribute("frameborder", "0");
        |    ifrm.setAttribute("tabindex", "0");
        |    ifrm.setAttribute("title", "Remarkbox");
        |    ifrm.style.width = "100%";
        |    document.getElementById("remarkbox-div").appendChild(ifrm);
        |  }
        |  create_remarkbox_iframe();
        |  iFrameResize(
        |    {
        |      checkOrigin: ["https://my.remarkbox.com"],
        |      inPageLinks: true,
        |      initCallback: function(e) {e.iFrameResizer.moveToAnchor(thread_fragment)}
        |    },
        |    document.getElementById("remarkbox-iframe")
        |  );
      """.stripMargin
    )
  ).render
}

object CommentsView {

  lazy val noscript = typedTag[dom.html.Head]("noscript")

  def apply(): CommentsView = new CommentsView()
}

