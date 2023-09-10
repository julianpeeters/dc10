package dc10.scala.ctx.predef

import cats.data.StateT
import dc10.scala.ast.Symbol.Term
import dc10.scala.ast.Symbol.Term.{TypeLevel, ValueLevel}
import dc10.scala.ast.Symbol.Term.ValueLevel.Var.UserDefinedValue
import dc10.scala.ast.Statement
import dc10.scala.ast.Statement.{ValDef, Expr}
import dc10.scala.ErrorF
import dc10.scala.ctx.ext
import org.tpolecat.sourcepos.SourcePos

trait Variables[F[_]]:
  def VAL[T](nme: String, tpe: F[Expr[TypeLevel, T]])(using sp: SourcePos): F[Expr[UserDefinedValue, T]]
  def VAL[T](nme: String, tpe: F[Expr[TypeLevel, T]])(impl: F[Expr[ValueLevel, T]])(using sp: SourcePos): F[Expr[UserDefinedValue, T]]
  given refV[T]: Conversion[Expr[ValueLevel, T], F[Expr[ValueLevel, T]]]

object Variables:

  trait Mixins extends Variables[[A] =>> StateT[ErrorF, List[Statement], A]]:

    def VAL[T](
      nme: String,
      tpe: StateT[ErrorF, List[Statement], Expr[TypeLevel, T]]
    )(
      using sp: SourcePos
    ): StateT[ErrorF, List[Statement], Expr[UserDefinedValue, T]] =
      for
        t <- tpe
        v <- StateT.pure[ErrorF, List[Statement], Expr[UserDefinedValue, T]](
          Expr.UserValue(Term.ValueLevel.Var.UserDefinedValue(nme, t.value, None)))
        d <- StateT.pure[ErrorF, List[Statement], ValDef](ValDef(v)(0))
        _ <- StateT.modifyF[ErrorF, List[Statement]](ctx => ctx.ext(d))
      yield v

    def VAL[T](
      nme: String,
      tpe: StateT[ErrorF, List[Statement], Expr[TypeLevel, T]]
    )( 
      impl: StateT[ErrorF, List[Statement], Expr[ValueLevel, T]]
    )(using sp: SourcePos): StateT[ErrorF, List[Statement], Expr[UserDefinedValue, T]] =
      for
        t <- tpe
        i <- impl
        v <- StateT.pure[ErrorF, List[Statement], Expr[UserDefinedValue, T]](
          Expr.UserValue(Term.ValueLevel.Var.UserDefinedValue(nme, t.value, Some(i.value)))
        )
        d <- StateT.pure[ErrorF, List[Statement], ValDef](ValDef(v)(0))
        _ <- StateT.modifyF[ErrorF, List[Statement]](ctx => ctx.ext(d))
      yield v

    given refV[T]: Conversion[Expr[ValueLevel, T], StateT[ErrorF, List[Statement], Expr[ValueLevel, T]]] =
      v => StateT.pure(v)

  