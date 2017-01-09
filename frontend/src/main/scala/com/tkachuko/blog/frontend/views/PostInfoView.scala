package com.tkachuko.blog.frontend.views

import com.tkachuko.blog.frontend.util.Util._
import com.tkachuko.blog.models.PostInfo
import org.scalajs.dom.Element

import scalatags.JsDom.all._

class PostInfoView(info: PostInfo) {

  def renderIn(container: Element) = {
    val tagsElementId = s"tags - ${info.title}"

    container.appendChild(
      div(`class` := "ui card",
        div(`class` := "content",
          a(`class` := "ui block header", info.title, onclick := PostView.onTitleClick(info.title)),
          div(`class` := "meta", span(s"Published ${info.created.readableDifference} ago"))
        ),
        div(`class` := "extra content", id := tagsElementId)
      ).render
    )

    info.tags.map(TagView.apply).foreach(_.renderInColor(tagsElementId.byId))
  }

}

object PostInfoView {

  def apply(info: PostInfo) = new PostInfoView(info)
}
