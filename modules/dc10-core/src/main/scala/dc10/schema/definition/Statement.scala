package dc10.schema.definition

import dc10.schema.{Binding}
import dc10.schema.Binding.{CaseClass, Package, Value}
import org.tpolecat.sourcepos.SourcePos

sealed trait Statement[A]
object Statement:

  trait CaseClassDef extends Statement[Binding]:
    type Tpe
    def caseclass: CaseClass[Tpe]
    def indent: Int
    def sourcePos: SourcePos
  
  object CaseClassDef:
    def apply[T](v: CaseClass[T], i: Int)(sp: SourcePos): CaseClassDef =
      new CaseClassDef:
        type Tpe = T
        def caseclass: CaseClass[T] = v
        def indent: Int = i
        def sourcePos: SourcePos = sp
  
  case class PackageDef(pkg: Package) extends Statement[Binding]
  
  trait ValDef extends Statement[Binding]:
    type Tpe
    def value: Value[Tpe]
    def indent: Int
    def sourcePos: SourcePos
  
  object ValDef:
    def apply[T](v: Value[T], i: Int)(sp: SourcePos): ValDef =
      new ValDef:
        type Tpe = T
        def value: Value[T] = v
        def indent: Int = i
        def sourcePos: SourcePos = sp
  
  // TODO: make into trait, add Tpe member
  case class UnsafeExpr(value: Value[String]) extends Statement[Binding]