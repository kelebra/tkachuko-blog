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

  private def loader = html(
    raw(
      """
        |<!-- Remarkbox - Your readers want to communicate with you -->
        |<div id="remarkbox-div">
        |  <noscript>
        |    <iframe id=remarkbox-iframe src="https://my.remarkbox.com/embed?nojs=true" style="height:600px;width:100%;border:none!important" tabindex=0></iframe>
        |  </noscript>
        |</div>
        |<script src="https://my.remarkbox.com/static/js/iframe-resizer/iframeResizer.min.js"></script>
        |<script>
        |  var rb_owner_key = "a0e80a2a-cf60-11e8-8ee2-040140774501";
        |  var thread_uri = window.location.href;
        |  var thread_fragment = window.location.hash;
        |  function create_remarkbox_iframe() {
        |    var src = "https://my.remarkbox.com/embed?rb_owner_key=" + rb_owner_key + "&thread_uri=" + thread_uri;
        |    var ifrm = document.createElement("iframe");
        |    ifrm.setAttribute("id", "remarkbox-iframe");
        |    ifrm.setAttribute("scrolling", "no");
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
        |</script>
      """.stripMargin
    )
  ).render
}

object CommentsView {

  def disqusUrl(title: String) = s"http://tkachuko.info/blog/${title.sanitize}"

  def apply(title: String) = new CommentsView(disqusUrl(title), title, title.hashCode)
}

