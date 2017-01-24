package com.logless

import java.util.UUID

/**
  * @author Maksim Ochenashko
  */
sealed trait TraceIdentifier

case class TraceID(id: UUID = UUID.randomUUID()) extends TraceIdentifier

case object DummyID extends TraceIdentifier

object TraceIdentifier {

  //tricky implicit
  implicit def generate: TraceIdentifier = DummyID

}