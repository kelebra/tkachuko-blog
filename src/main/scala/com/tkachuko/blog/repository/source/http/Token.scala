package com.tkachuko.blog.repository.source.http

sealed abstract class Token(val parameter: String, val value: String)

case object MLabToken extends Token("apiKey", "lszijr65VV8oxrkkwPIolGFG0zpgpZhW")
