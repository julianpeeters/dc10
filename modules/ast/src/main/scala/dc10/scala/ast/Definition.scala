package dc10.scala.ast

import dc10.scala.ast.Binding.{Record, Package, Term}
import org.tpolecat.sourcepos.SourcePos

sealed trait Definition

object Definition:

  // case class ScalaFile(file: File) extends Definition

  sealed trait Statement extends Definition:
    def indent: Int
    def sp: SourcePos
   
  object Statement:

    // def indent(s: Statement): Statement =
    //   s match
    //     case b@ObjectDef(_, _, i) => b.copy(indent = i + 1)
    //     case b@PackageDef(_, _, i) => b.copy(indent = i + 1)
    //     case b@TypeDef(_, _, i) => b.copy(indent = i + 1)
    //     case b@DefDef(_, _, i) => b.copy(indent = i + 1)
    //     case b@ValDef(_, _, i) => b.copy(indent = i + 1)

    // case class PackageDef(
    //   pkg: Package,
    //   sp: SourcePos,
    //   indent: Int,
    // ) extends Statement
  

    sealed abstract case class RecordDef(
      indent: Int,
      sp: SourcePos
    ) extends Statement:
      type Arg
      type Tpe
      def caseclass: Record[Tpe, Arg]

    object RecordDef:
      def apply[T, A](
        v: Record[T, A],
        i: Int
      )(
        using sp: SourcePos
      ): RecordDef =
        new RecordDef(i, sp):
          type Arg = A
          type Tpe = T
          def caseclass: Record[T, A] = v

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