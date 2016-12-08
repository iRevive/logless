package com.logless

import scala.language.higherKinds

/**
  * @author Maksim Ochenashko
  */
trait Loggable[X] {

  def present(value: X): String

}

object Loggable {

  @inline def apply[X](implicit F: Loggable[X]): Loggable[X] = F

  def fromString[X]: Loggable[X] = Loggable.presents(_.toString)

  def presents[X](f: X => String): Loggable[X] = new Loggable[X] {
    override def present(value: X): String = f(value)
  }

  implicit val byteLoggable: Loggable[Byte] = Loggable.fromString
  implicit val charLoggable: Loggable[Char] = Loggable.fromString
  implicit val shortLoggable: Loggable[Short] = Loggable.fromString
  implicit val intLoggable: Loggable[Int] = Loggable.fromString
  implicit val longLoggable: Loggable[Long] = Loggable.fromString
  implicit val doubleLoggable: Loggable[Double] = Loggable.fromString
  implicit val floatLoggable: Loggable[Float] = Loggable.fromString
  implicit val bigDecimalLoggable: Loggable[BigDecimal] = Loggable.fromString
  implicit val stringLoggable: Loggable[String] = Loggable.fromString

  implicit def iterableLoggable[CC[X] <: Iterable[X], A: Loggable]: Loggable[CC[A]] = new Loggable[CC[A]] {

    override def present(value: CC[A]): String = value.map(Loggable[A].present(_)).mkString("[", ", ", "]")

  }


}