package com.logless.builder

import com.logless.builder

import scala.language.implicitConversions

/**
  * @author Maksim Ochenashko
  */
sealed trait LogBuilder {

  override def toString: String = print

  final def print: String = asList.mkString(", ")

  protected[logless] def asList: List[String]
}

final case class :+:[+H: Loggable, +T <: LogBuilder](head: H, tail: T) extends LogBuilder {

  override protected[logless] def asList: List[String] =
    implicitly[Loggable[H]].present(head) :: tail.asList

}

sealed trait End extends LogBuilder {
  def :+:[H: Loggable](h: H): H :+: End = builder.:+:(h, this)

  override protected[logless] def asList: List[String] = Nil
}

case object End extends End

object LogBuilder {

  def apply(): End = End

  def apply[X: Loggable](v: X): X :+: End = v :+: End

  implicit def logBuilderOps[X <: LogBuilder](builder: X): LogBuilderOps[X] = new LogBuilderOps[X](builder)

  implicit def toBuilderOps[X: Loggable](value: X): LogBuilderOps[X :+: End] =
    new LogBuilderOps(LogBuilder(value))

}


