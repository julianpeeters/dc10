package dc10.scala.ctx.predef

import cats.data.StateT
import dc10.scala.ast.Binding
import dc10.scala.ast.Binding.Term
import dc10.scala.ast.Definition.Statement
import dc10.scala.ast.Definition.Statement.ValDef
import dc10.scala.ctx.ext
import dc10.Compiler.ErrorF
import org.tpolecat.sourcepos.SourcePos

trait Variables[F[_]]:
  def VAL[T](nme: String, tpe: F[Term.TypeLevel[T]])(using sp: SourcePos): F[Term.ValueLevel.Var.UserDefinedValue[T]]
  def VAL[T](nme: String, tpe: F[Term.TypeLevel[T]])(impl: F[Term.ValueLevel[T]])(using sp: SourcePos): F[Term.ValueLevel.Var.UserDefinedValue[T]]
  given refV[T]: Conversion[Term.ValueLevel[T], F[Term.ValueLevel[T]]]

object Variables:

  trait Mixins extends Variables[[A] =>> StateT[ErrorF, List[Statement], A]]:

    def VAL[T](
      nme: String,
      tpe: StateT[ErrorF, List[Statement], Term.TypeLevel[T]]
    )(
      using sp: SourcePos
    ): StateT[ErrorF, List[Statement], Term.ValueLevel.Var.UserDefinedValue[T]] =
      for
        t <- tpe
        v <- StateT.pure[ErrorF, List[Statement], Term.ValueLevel.Var.UserDefinedValue[T]](
          Term.ValueLevel.Var.UserDefinedValue(nme, t, None))
        d <- StateT.pure[ErrorF, List[Statement], ValDef](ValDef(v)(0))
        _ <- StateT.modifyF[ErrorF, List[Statement]](ctx => ctx.ext(d))
      yield v

    def VAL[T](
      nme: String,
      tpe: StateT[ErrorF, List[Statement], Term.TypeLevel[T]]
    )( 
      impl: StateT[ErrorF, List[Statement], Term.ValueLevel[T]]
    )(using sp: SourcePos): StateT[ErrorF, List[Statement], Term.ValueLevel.Var.UserDefinedValue[T]] =
      for
        t <- tpe
        i <- impl
        v <- StateT.pure[ErrorF, List[Statement], Term.ValueLevel.Var.UserDefinedValue[T]](
          Term.ValueLevel.Var.UserDefinedValue(nme, t, Some(i)))
        d <- StateT.pure[ErrorF, List[Statement], ValDef](ValDef(v)(0))
        _ <- StateT.modifyF[ErrorF, List[Statement]](ctx => ctx.ext(d))
      yield v

    given refV[T]: Conversion[Term.ValueLevel[T], StateT[ErrorF, List[Statement], Term.ValueLevel[T]]] =
      StateT.pure

  