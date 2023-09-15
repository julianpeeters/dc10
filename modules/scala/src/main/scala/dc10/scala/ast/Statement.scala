package dc10.scala.ast

import Symbol.{CaseClass, Package, Term}
import org.tpolecat.sourcepos.SourcePos
import Symbol.Term.ValueLevel


sealed trait Statement:
  def indent: Int
  def sp: SourcePos
  
object Statement:

  sealed abstract case class CaseClassDef(
    indent: Int,
    sp: SourcePos
  ) extends Statement:
    type Tpe
    def caseclass: CaseClass[Tpe]

  object CaseClassDef:
    def apply[T](
      v: CaseClass[T],
      i: Int
    )(
      using sp: SourcePos
    ): CaseClassDef =
      new CaseClassDef(i, sp):
        type Tpe = T
        def caseclass: CaseClass[T] = v

  case class ObjectDef(
    module: Object,
    indent: Int,
    sp: SourcePos
  ) extends Statement

  sealed abstract case class PackageDef(
    indent: Int,
    sp: SourcePos
  ) extends Statement:
    def pkg: Package

  object PackageDef:
    def apply[T](
      i: Int,
      p: Package
    )(
      using sp: SourcePos
    ): PackageDef =
      new PackageDef(i, sp):
        def pkg: Package = p

  sealed abstract case class ValDef(
    indent: Int,
    sp: SourcePos
  ) extends Statement:
    type Tpe
    def value: ValueLevel.Var.UserDefinedValue[Tpe]

  object ValDef:
    def apply[T](
      v: ValueLevel.Var.UserDefinedValue[T]
    )(
      i: Int
    )(
      using sp: SourcePos
    ): ValDef =
      new ValDef(i, sp):
        type Tpe = T
        def value: ValueLevel.Var.UserDefinedValue[T] = v


  case class TypeExpr[T](tpe: Term.TypeLevel[T]) extends Statement:
    def indent: Int = 0
    def sp: SourcePos = summon[SourcePos]

  case class ValueExpr[T](value: Term.ValueLevel[T]) extends Statement:
    def indent: Int = 0
    def sp: SourcePos = summon[SourcePos]