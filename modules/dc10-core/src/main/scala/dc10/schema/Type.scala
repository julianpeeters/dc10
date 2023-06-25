package dc10.schema

sealed abstract class Type:
  def nme: String
  def impl: Option[Type]

object Type:
  
  def apply(n: String, i: Option[Type]): Type =
    new Type:
      def nme = n
      def impl = i

  def string: Type =
    new Type:
      def nme: String = "String"
      def impl = None