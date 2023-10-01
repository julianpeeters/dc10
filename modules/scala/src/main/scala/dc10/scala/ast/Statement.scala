package dc10.scala.ast

import org.tpolecat.sourcepos.SourcePos
import Symbol.{CaseClass, Object, Package, Term}

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

  sealed abstract case class ObjectDef(
    indent: Int,
    sp: SourcePos
  ) extends Statement:
    type Tpe
    def obj: Object[Tpe]

  object ObjectDef:
    def apply[T](
      o: Object[T],
      i: Int
    )(
      using sp: SourcePos
    ): ObjectDef =
      new ObjectDef(i, sp):
        type Tpe = T
        def obj: Object[T] = o

  sealed abstract case class PackageDef(
    indent: Int,
    sp: SourcePos
  ) extends Statement:
    def pkg: Package

  object PackageDef:
    def apply[T](
      p: Package,
      i: Int,
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