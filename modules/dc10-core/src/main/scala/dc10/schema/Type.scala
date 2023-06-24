package dc10.schema

sealed abstract class Type:
  def nme: String
  def impl: Option[Type]

object Type:
  case object STRING extends Type:
    def nme: String = "String"
    def impl = None