package com.tkachuko.blog.frontend.views

import org.scalajs.dom.Element
import org.scalajs.dom.html.{Div, Script}
import scalatags.JsDom.all._

class CommentsView(title: String) {

  def render(container: Element) = {
    container.appendChild(commentsContainer)
    container.appendChild(loader)
  }

  private def commentsContainer: Div = div(id := "disqus_thread", `class` := "item").render

  private def loader = script(
    raw(
      s"""
         |    var disqus_config = function () {
         |        this.page.url = '';
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

  private def setup: Script =
    script(src := "https://cdn.commento.io/js/commento.js").render
}

object CommentsView {

  def apply(title: String) = new CommentsView(title)
}

