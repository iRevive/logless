/**
  * @author Maksim Ochenashko
  */
package object macrolog {

  type TraceId = macrolog.TraceQualifier.TraceId
  type Position = macrolog.auto.Position

  val TraceId = macrolog.TraceQualifier.TraceId
  val Position = macrolog.auto.Position

}
