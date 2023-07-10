package dc10.ast

import dc10.ast.Binding.{CaseClass, File, Package, Term}
import org.tpolecat.sourcepos.SourcePos

sealed trait Definition

object Definition:

  case class SourceFile(file: File) extends Definition

  sealed trait Statement[A] extends Definition:
    def indent: Int
    def sp: SourcePos
   
  object Statement:

    sealed abstract case class CaseClassDef(
      indent: Int,
      sp: SourcePos
    ) extends Statement[Binding]:
      type Arg
      type Tpe
      def caseclass: CaseClass[Tpe, Arg]

    object CaseClassDef:
      def apply[T, A](
        v: CaseClass[T, A],
        i: Int
      )(
        using sp: SourcePos
      ): CaseClassDef =
        new CaseClassDef(i, sp):
          type Arg = A
          type Tpe = T
          def caseclass: CaseClass[T, A] = v

    sealed abstract case class PackageDef(
      indent: Int,
      sp: SourcePos
    ) extends Statement[Binding]:
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
    ) extends Statement[Binding]:
      type Tpe
      def value: Term.ValueLevel.Var.UserDefinedValue[Tpe]

    object ValDef:
      def apply[T](
        v: Term.ValueLevel.Var.UserDefinedValue[T]
      )(
        i: Int
      )(
        using sp: SourcePos
      ): ValDef =
        new ValDef(i, sp):
          type Tpe = T
          def value: Term.ValueLevel.Var.UserDefinedValue[T] = v