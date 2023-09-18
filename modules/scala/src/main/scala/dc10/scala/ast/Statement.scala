package dc10.scala.ast

import org.tpolecat.sourcepos.SourcePos
import Symbol.{CaseClass, Package, Term}

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
    type Xx
    def value: Term.Value[Tpe]

  object ValDef:
    def apply[T, X](
      v: Term.Value[T]
    )(
      i: Int
    )(
      using sp: SourcePos
    ): ValDef =
      new ValDef(i, sp):
        type Tpe = T
        type Xx = X
        def value: Term.Value[T] = v


  case class TypeExpr[T](tpe: Term.Type[T]) extends Statement:
    def indent: Int = 0
    def sp: SourcePos = summon[SourcePos]

  case class ValueExpr[T](value: Term.Value[T]) extends Statement:
    def indent: Int = 0
    def sp: SourcePos = summon[SourcePos]