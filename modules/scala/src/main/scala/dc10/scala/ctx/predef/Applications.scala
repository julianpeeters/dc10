package dc10.scala.ctx.predef

import cats.implicits.*
import cats.data.StateT
import cats.Eval
import cats.free.Cofree
import dc10.scala.ast.Symbol.Term
import dc10.scala.ast.Symbol.Term.TypeLevel.__
import dc10.scala.ast.Symbol.Term.{TypeLevel, ValueLevel}
import dc10.scala.ast.Statement
import dc10.scala.ast.Statement.{TypeExpr, ValueExpr}
import dc10.scala.error.ErrorF

trait Applications[F[_]]:

  extension [T[_]] (function: F[TypeExpr[T[__]]])
    @scala.annotation.targetName("app1T")
    def apply[A](args: F[TypeExpr[A]]): F[TypeExpr[T[A]]]

  extension [T[_,_]] (tfunction: F[TypeExpr[T[__, __]]])
    @scala.annotation.targetName("app2T")
    def apply[A, B](fta: F[TypeExpr[A]])(ftb: F[TypeExpr[B]]): F[TypeExpr[T[A, B]]]

  extension [A, B] (function: F[ValueExpr[A => B]])
    @scala.annotation.targetName("app1V")
    def apply(args: F[ValueExpr[A]]): F[ValueExpr[B]]

object Applications:

  trait Mixins extends Applications[[A] =>> StateT[ErrorF, List[Statement], A]]:

    extension [T[_]] (tfunction: StateT[ErrorF, List[Statement], TypeExpr[T[__]]])
      @scala.annotation.targetName("app1T")
      def apply[A](targs: StateT[ErrorF, List[Statement], TypeExpr[A]]): StateT[ErrorF, List[Statement], TypeExpr[T[A]]] =
        for
          f <- tfunction
          a <- targs
        yield TypeExpr(Term.TypeLevel.App1[T, A](f.tpe, a.tpe))

    extension [T[_,_]] (tfunction: StateT[ErrorF, List[Statement], TypeExpr[T[__, __]]])
      @scala.annotation.targetName("app2T")
      def apply[A, B](fta: StateT[ErrorF, List[Statement], TypeExpr[A]])(ftb: StateT[ErrorF, List[Statement], TypeExpr[B]]): StateT[ErrorF, List[Statement], TypeExpr[T[A, B]]] =
        for
          f <- tfunction
          a <- fta
          b <- ftb
        yield TypeExpr(Term.TypeLevel.App2[T, A, B](f.tpe, a.tpe, b.tpe))

    extension [A, B] (function: StateT[ErrorF, List[Statement], ValueExpr[A => B]])
      @scala.annotation.targetName("app1V")
      def apply(args: StateT[ErrorF, List[Statement], ValueExpr[A]]): StateT[ErrorF, List[Statement], ValueExpr[B]] =
        for
          f <- function
          a <- args
        yield ValueExpr(Cofree((), Eval.now(Term.ValueLevel.App1(f.value, a.value))))