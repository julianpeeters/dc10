package dc10.schema

case class Value(nme: String, tpe: Type, impl: Option[Value])