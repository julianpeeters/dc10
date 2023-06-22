package dc10.schema

sealed trait Type:
  def nme: String
  def impl: Option[Type]
object Type:
  case object String extends Type:
    def nme: Predef.String = "String"
    def impl = None