package dc10.schema

sealed abstract class Value:
  def nme: String
  def tpe: Type
  def impl: Option[Value]

object Value:
  
  def apply(
    n: String,
    t: Type,
    i: Option[Value]
  ): Value =
    new Value:
      def nme: String = s"${n}"
      def tpe: Type = t
      def impl: Option[Value] = i

  def string(s: String): Value =
    new Value:
      def nme: String = s"\"${s}\""
      def tpe: Type = Type.string
      def impl = None