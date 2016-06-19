package com.tkachuko.blog

package object backend {

  implicit class URLOps(value: String) {

    def withoutHttpSpaces = value.replace("%20", " ")

    def comaSeparatedList = value.split(",").map(_.trim).toList
  }
}
