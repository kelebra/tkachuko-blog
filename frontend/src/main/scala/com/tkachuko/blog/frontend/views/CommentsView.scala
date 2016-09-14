package com.tkachuko.blog.frontend.views

import com.tkachuko.blog.frontend.router.Router._
import org.scalajs.dom.Element

import scalatags.JsDom.all._

class CommentsView(title: String, thread: String) {

  def render(container: Element) = {
    container.appendChild(commentsContainer)
    container.appendChild(loader)
  }

  private def commentsContainer = div(id := "disqus_thread").render

  private def loader = script(
    raw(
      s"""
         |    var disqus_config = function () {
         |        this.page.url = 'http://tkachuko.info/blog/$thread';
         |        this.page.identifier = '${title.hashCode}';
         |        this.page.title = '$title';
         |    };
         |
         |    (function() {
         |        var d = document, s = d.createElement('script');
         |
         |        s.src = '//tkachuko-info.disqus.com/embed.js';
         |
         |        s.setAttribute('data-timestamp', +new Date());
         |        (d.head || d.body).appendChild(s);
         |    })();
    """.stripMargin
    )
  ).render
}

object CommentsView {

  def apply(title: String) = new CommentsView(title, title.sanitize)
}

