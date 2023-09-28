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
  def VAL[T](decl: (String, F[TypeExpr[T]])): F[ValueExpr[T]]
  def VAL[T](defn: (String, F[TypeExpr[T]], F[ValueExpr[T]])): F[ValueExpr[T]]
  given refV[T]: Conversion[ValueExpr[T], F[ValueExpr[T]]]
  extension [T] (nme: String)
    def ::(tpe: F[TypeExpr[T]])(using sp: SourcePos): (String, F[TypeExpr[T]])
  extension [T] (decl: (String, F[TypeExpr[T]]))
    def :=(impl: F[ValueExpr[T]])(using sp: SourcePos): (String, F[TypeExpr[T]], F[ValueExpr[T]])

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

    def VAL[T](
      decl: (String, StateT[ErrorF, List[Statement], TypeExpr[T]])
    ): StateT[ErrorF, List[Statement], ValueExpr[T]] =
      for
        t <- decl._2
        v <- StateT.pure[ErrorF, List[Statement], Term.Value[T]](Cofree((), Eval.now(Term.ValueLevel.Var.UserDefinedValue(None, decl._1, t.tpe, None))))
        d <- StateT.pure[ErrorF, List[Statement], ValDef](ValDef(v)(0))
        _ <- StateT.modifyF[ErrorF, List[Statement]](ctx => ctx.ext(d))
      yield ValueExpr(v)

    def VAL[T](defn: (String, StateT[ErrorF, List[Statement], TypeExpr[T]], StateT[ErrorF, List[Statement], ValueExpr[T]])): StateT[ErrorF, List[Statement], ValueExpr[T]] =
      for
        t <- defn._2
        i <- defn._3
        v <- StateT.pure[ErrorF, List[Statement], Term.Value[T]](Cofree((), Eval.now(Term.ValueLevel.Var.UserDefinedValue(None, defn._1, t.tpe, Some(i.value)))))
        d <- StateT.pure[ErrorF, List[Statement], ValDef](ValDef(v)(0))
        _ <- StateT.modifyF[ErrorF, List[Statement]](ctx => ctx.ext(d))
      yield ValueExpr(v)

    given refV[T]: Conversion[ValueExpr[T], StateT[ErrorF, List[Statement], ValueExpr[T]]] =
      v => StateT.pure(v)

    extension [T] (nme: String)
      def ::(tpe: StateT[ErrorF, List[Statement], TypeExpr[T]])(using sp: SourcePos): (String, StateT[ErrorF, List[Statement], TypeExpr[T]]) =
        (nme, tpe)

    extension [T] (decl: (String, StateT[ErrorF, List[Statement], TypeExpr[T]]))
      def :=(impl: StateT[ErrorF, List[Statement], ValueExpr[T]])(
        using sp: SourcePos
      ): (String, StateT[ErrorF, List[Statement], TypeExpr[T]], StateT[ErrorF, List[Statement], ValueExpr[T]])
       = (decl._1, decl._2, impl)