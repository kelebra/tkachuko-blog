package com.tkachuko.blog.service.markdown

private[markdown] object Util {

  implicit class StringOps(input: String) {

    def optionalIndexOf(other: String, from: Int): Option[Int] =
      Option(input.indexOf(other, from)).filterNot(_ == -1)
  }

}
