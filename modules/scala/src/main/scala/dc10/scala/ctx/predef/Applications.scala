package dc10.scala.ctx.predef

import cats.implicits.*
import cats.data.StateT
import dc10.scala.ast.Symbol.Term
import dc10.scala.ast.Symbol.Term.TypeLevel.__
import dc10.scala.ast.Symbol.Term.{TypeLevel, ValueLevel}
import dc10.scala.ast.Statement
import dc10.scala.ast.Statement.Expr
import dc10.scala.ctx.ErrorF

trait Applications[F[_]]:

  extension [T[_]] (function: F[Expr[TypeLevel, T[__]]])
    @scala.annotation.targetName("app1T")
    def apply[A](args: F[Expr[TypeLevel, A]]): F[Expr[TypeLevel, T[A]]]

  extension [T[_,_]] (tfunction: F[Expr[TypeLevel, T[__, __]]])
    @scala.annotation.targetName("app2T")
    def apply[A, B](fta: F[Expr[TypeLevel, A]])(ftb: F[Expr[TypeLevel, B]]): F[Expr[TypeLevel, T[A, B]]]

  extension [A, B] (function: F[Expr[ValueLevel, A => B]])
    @scala.annotation.targetName("app1V")
    def apply(args: F[Expr[ValueLevel, A]]): F[Expr[ValueLevel, B]]

object Applications:

  trait Mixins extends Applications[[A] =>> StateT[ErrorF, List[Statement], A]]:

    extension [T[_]] (tfunction: StateT[ErrorF, List[Statement], Expr[TypeLevel, T[__]]])
      @scala.annotation.targetName("app1T")
      def apply[A](targs: StateT[ErrorF, List[Statement], Expr[TypeLevel, A]]): StateT[ErrorF, List[Statement], Expr[TypeLevel, T[A]]] =
        for
          f <- tfunction
          a <- targs
        yield Expr.BuiltInType(Term.TypeLevel.App1[T, A](f, a))

    extension [T[_,_]] (tfunction: StateT[ErrorF, List[Statement], Expr[TypeLevel, T[__, __]]])
      @scala.annotation.targetName("app2T")
      def apply[A, B](fta: StateT[ErrorF, List[Statement], Expr[TypeLevel, A]])(ftb: StateT[ErrorF, List[Statement], Expr[TypeLevel, B]]): StateT[ErrorF, List[Statement], Expr[TypeLevel, T[A, B]]] =
        for
          f <- tfunction
          a <- fta
          b <- ftb
        yield Expr.BuiltInType(Term.TypeLevel.App2[T, A, B](f, a, b))

    extension [A, B] (function: StateT[ErrorF, List[Statement], Expr[ValueLevel, A => B]])
      @scala.annotation.targetName("app1V")
      def apply(args: StateT[ErrorF, List[Statement], Expr[ValueLevel, A]]): StateT[ErrorF, List[Statement], Expr[ValueLevel, B]] =
        for
          f <- function
          a <- args
        yield Expr.BuiltInValue(Term.ValueLevel.App1[A, B](f, a))