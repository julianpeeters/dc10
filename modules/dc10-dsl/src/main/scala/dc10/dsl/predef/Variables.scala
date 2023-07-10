package dc10.dsl.predef

import cats.data.StateT
import dc10.compile.Compiler
import dc10.compile.Compiler.{Γ, ErrorF, ext}
import dc10.ast.Binding.Term
import dc10.ast.Definition.Statement.ValDef
import org.tpolecat.sourcepos.SourcePos
import dc10.ast.predef.Variable

trait Variables[F[_]]:
  def VAL[T](nme: String, tpe: F[Term.TypeLevel[T]])(using sp: SourcePos): F[Term.ValueLevel.Var.UserDefinedValue[T]]
  def VAL[T](nme: String, tpe: F[Term.TypeLevel[T]])(impl: F[Term.ValueLevel[T]])(using sp: SourcePos): F[Term.ValueLevel.Var.UserDefinedValue[T]]
  given refV[T]: Conversion[Term.ValueLevel[T], F[Term.ValueLevel[T]]]

object Variables:


  trait Mixins extends Variables[[A] =>> StateT[ErrorF, Γ, A]]:

    def VAL[T](
      nme: String,
      tpe: StateT[ErrorF, Γ, Term.TypeLevel[T]]
    )(
      using sp: SourcePos
    ): StateT[ErrorF, Γ, Term.ValueLevel.Var.UserDefinedValue[T]] =
      for
        v <- StateT.liftF[ErrorF, Γ, Term.ValueLevel.Var.UserDefinedValue[T]](
          Variable[ErrorF].declare(nme, tpe.runEmptyA))
        d <- StateT.pure[ErrorF, Γ, ValDef](ValDef(v)(0))
        _ <- StateT.modifyF[ErrorF, Γ](ctx => ctx.ext(d))
      yield v

    def VAL[T](
      nme: String,
      tpe: StateT[ErrorF, Γ, Term.TypeLevel[T]]
    )( 
      impl: StateT[ErrorF, Γ, Term.ValueLevel[T]]
    )(using sp: SourcePos): StateT[ErrorF, Γ, Term.ValueLevel.Var.UserDefinedValue[T]] =
      for
        v <- StateT.liftF[ErrorF, Γ, Term.ValueLevel.Var.UserDefinedValue[T]](
          Variable[ErrorF].define(nme, tpe.runEmptyA, impl.runEmptyA))
        d <- StateT.pure[ErrorF, Γ, ValDef](ValDef(v)(0))
        _ <- StateT.modifyF[ErrorF, Γ](ctx => ctx.ext(d))
      yield v

    given refV[T]: Conversion[Term.ValueLevel[T], StateT[ErrorF, Γ, Term.ValueLevel[T]]] =
      StateT.pure

  