package dc10.scala.ast

import dc10.scala.ast.Binding.{CaseClass, Package, Term}
import org.tpolecat.sourcepos.SourcePos
import dc10.scala.ast.Binding.Term.ValueLevel


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
    def value: Expr[Term.ValueLevel.Var.UserDefinedValue, Tpe]

  object ValDef:
    def apply[T](
      v: Expr[Term.ValueLevel.Var.UserDefinedValue, T]
    )(
      i: Int
    )(
      using sp: SourcePos
    ): ValDef =
      new ValDef(i, sp):
        type Tpe = T
        def value: Expr[Term.ValueLevel.Var.UserDefinedValue, T] = v


  sealed trait Expr[+F[_], T] extends Statement:
    type Tpe = T
    def value: F[Tpe]

  object Expr:

    case class BuiltInType[T](value: Term.TypeLevel[T]) extends Expr[Term.TypeLevel, T]:
      def indent: Int = 0
      def sp: SourcePos = summon[SourcePos]

    case class BuiltInValue[T](value: Term.ValueLevel[T]) extends Expr[Term.ValueLevel, T]:
      def indent: Int = 0
      def sp: SourcePos = summon[SourcePos]
     
    case class UserType[T](value: Term.TypeLevel.Var.UserDefinedType[T]) extends Expr[Term.TypeLevel.Var.UserDefinedType, T]:
      def indent: Int = 0
      def sp: SourcePos = summon[SourcePos]

    case class UserValue[T](value: Term.ValueLevel.Var.UserDefinedValue[T]) extends Expr[Term.ValueLevel.Var.UserDefinedValue, T]:
      def indent: Int = 0
      def sp: SourcePos = summon[SourcePos]

      