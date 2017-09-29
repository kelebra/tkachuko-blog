package com.tkachuko.blog.repository.source.http

sealed abstract class Token(val parameter: String, val value: String)

case class MLabToken(override val value: String) extends Token ("apiKey", value)
