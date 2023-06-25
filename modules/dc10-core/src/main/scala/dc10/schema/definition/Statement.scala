package dc10.schema.definition

import dc10.schema.{CaseClass, Package, Value}
import org.tpolecat.sourcepos.SourcePos

sealed trait Statement
object Statement:
  case class CaseClassDef(caseclass: CaseClass, indent: Int)(sp: SourcePos) extends Statement
  case class PackageDef(pkg: Package) extends Statement
  case class ValDef(value: Value, indent: Int)(sp: SourcePos) extends Statement
  case class UnsafeExpr(value: Value) extends Statement