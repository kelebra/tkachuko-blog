package com.tkachuko.blog.frontend.views

import com.tkachuko.blog.frontend.router.Router._
import org.scalajs.dom.Element

import scalatags.JsDom.all._

class CommentsView(url: String, title: String, identifier: Int) {

  def render(container: Element) = {
    container.appendChild(commentsContainer)
    container.appendChild(loader)
  }

  private def commentsContainer = div(id := "disqus_thread", `class` := "item").render

  private def loader = script(
    raw(
      s"""
         |    var disqus_config = function () {
         |        this.page.url = '$url';
         |        this.page.identifier = '$identifier';
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

  def disqusUrl(title: String) = s"http://tkachuko.info/blog/${title.sanitize}"

  def apply(title: String) = new CommentsView(disqusUrl(title), title, title.hashCode)
}

