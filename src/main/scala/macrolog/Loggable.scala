package macrolog

import java.util.UUID

import simulacrum.typeclass

import scala.annotation.implicitNotFound
import scala.language.implicitConversions

/**
  * @author Maksim Ochenashko
  */
@implicitNotFound(
  """
 No Loggable found for type ${A}. Try to implement an implicit Loggable[${A}].
 You can implement it in ${A} companion class.
    """
)
@typeclass
trait Loggable[A] {

  def print(value: A): String

}

object Loggable extends LoggableInstances {

  def instance[A](op: A => String): Loggable[A] = (value: A) => op(value)

}

trait LoggableInstances {

  import Loggable.ops._

  implicit val stringLoggable   : Loggable[String]     = Loggable.instance(identity)
  implicit val intLoggable      : Loggable[Int]        = toStringLoggable
  implicit val shortLoggable    : Loggable[Short]        = toStringLoggable
  implicit val longLoggable     : Loggable[Long]       = toStringLoggable
  implicit val doubleLoggable   : Loggable[Double]     = toStringLoggable
  implicit val floatLoggable    : Loggable[Float]      = toStringLoggable
  implicit val booleanLoggable  : Loggable[Boolean]    = toStringLoggable
  implicit val uuidLoggable     : Loggable[UUID]       = toStringLoggable

  implicit val posLoggable      : Loggable[Position]   = Loggable.instance(v => s"Pos(${v.fullPosition})")

  implicit val throwableLoggable: Loggable[Throwable] =
    Loggable instance { throwable =>
      if (throwable.getMessage == null) s"${throwable.getClass.getSimpleName}(<empty message>)"
      else s"${throwable.getClass.getSimpleName}(${throwable.getMessage})"
    }

  implicit def listLoggable[A: Loggable]: Loggable[List[A]] =
    Loggable instance { value =>
      value.map(Loggable[A].print).mkString("[", ", ", "]")
    }

  implicit def seqLoggable[A: Loggable]: Loggable[Seq[A]] =
    Loggable instance { value =>
      value.map(Loggable[A].print).mkString("[", ", ", "]")
    }

  implicit def mapLoggable[A: Loggable, B: Loggable]: Loggable[Map[A, B]] =
    Loggable instance { value =>
      value.toList.map(Loggable[(A, B)].print).mkString("[", ", ", "]")
    }

  implicit def optionLoggable[A: Loggable]: Loggable[Option[A]] =
    Loggable instance { value =>
      value.fold("None")(v => s"Some(${v.print})")
    }

  implicit def tuple2[A: Loggable, B: Loggable]: Loggable[(A, B)] =
    Loggable instance { case (first, second) =>
      s"(${first.print}, ${second.print})"
    }

  implicit def tuple3[A: Loggable, B: Loggable, C: Loggable]: Loggable[(A, B, C)] =
    Loggable instance { case (first, second, third) =>
      s"(${first.print}, ${second.print}, ${third.print})"
    }

  implicit def tuple4[A: Loggable, B: Loggable, C: Loggable, D: Loggable]: Loggable[(A, B, C, D)] =
    Loggable instance { case (first, second, third, fourth) =>
      s"(${first.print}, ${second.print}, ${third.print}, ${fourth.print})"
    }

  implicit def eitherLoggable[A: Loggable, B: Loggable]: Loggable[Either[A, B]] =
    Loggable instance {
      case Left(value)  => s"Left(${value.print})"
      case Right(value) => s"Right(${value.print})"
    }

  def toStringLoggable[A]: Loggable[A] =
    Loggable.instance(_.toString)

}