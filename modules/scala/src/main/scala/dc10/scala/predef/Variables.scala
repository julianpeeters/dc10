package dc10.scala.predef

import cats.data.StateT
import cats.Eval
import cats.free.Cofree
import dc10.scala.ast.Statement
import dc10.scala.ast.Statement.{TypeExpr, ValDef, ValueExpr}
import dc10.scala.ast.Symbol.Term
import dc10.scala.ctx.ext
import dc10.scala.error.ErrorF
import org.tpolecat.sourcepos.SourcePos

trait Variables[F[_]]:
  def VAL[T](nme: String, tpe: F[TypeExpr[T]])(using sp: SourcePos): F[ValueExpr[T]]
  def VAL[T](nme: String, tpe: F[TypeExpr[T]], impl: F[ValueExpr[T]])(using sp: SourcePos): F[ValueExpr[T]]
  given refV[T]: Conversion[ValueExpr[T], F[ValueExpr[T]]]

object Variables:

  trait Mixins extends Variables[[A] =>> StateT[ErrorF, List[Statement], A]]:

    def VAL[T](
      nme: String,
      tpe: StateT[ErrorF, List[Statement], TypeExpr[T]]
    )(
      using sp: SourcePos
    ): StateT[ErrorF, List[Statement], ValueExpr[T]] =
      for
        t <- tpe
        v <- StateT.pure[ErrorF, List[Statement], Term.Value[T]](Cofree((), Eval.now(Term.ValueLevel.Var.UserDefinedValue(None, nme, t.tpe, None))))
        d <- StateT.pure[ErrorF, List[Statement], ValDef](ValDef(v)(0))
        _ <- StateT.modifyF[ErrorF, List[Statement]](ctx => ctx.ext(d))
      yield ValueExpr(v)

    def VAL[T](
      nme: String,
      tpe: StateT[ErrorF, List[Statement], TypeExpr[T]], 
      impl: StateT[ErrorF, List[Statement], ValueExpr[T]]
    )(using sp: SourcePos): StateT[ErrorF, List[Statement], ValueExpr[T]] =
      for
        t <- tpe
        i <- impl
        v <- StateT.pure[ErrorF, List[Statement], Term.Value[T]](Cofree((), Eval.now(Term.ValueLevel.Var.UserDefinedValue(None, nme, t.tpe, Some(i.value)))))
        d <- StateT.pure[ErrorF, List[Statement], ValDef](ValDef(v)(0))
        _ <- StateT.modifyF[ErrorF, List[Statement]](ctx => ctx.ext(d))
      yield ValueExpr(v)

    given refV[T]: Conversion[ValueExpr[T], StateT[ErrorF, List[Statement], ValueExpr[T]]] =
      v => StateT.pure(v)