package dc10.schema.define

import dc10.schema.{CaseClass, Package, Value}

sealed trait Statement
case class CaseClassDef(caseclass: CaseClass) extends Statement
case class PackageDef(pkg: Package) extends Statement
case class ValDef(value: Value) extends Statement
case class UnsafeExpr(value: Value) extends Statement