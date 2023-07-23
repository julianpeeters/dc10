package dc10.scala.ctx.predef

import cats.implicits.*
import cats.data.StateT
import dc10.scala.ast.Binding
import dc10.scala.ast.Binding.Term
import dc10.scala.ast.Binding.Term.TypeLevel.__
import dc10.scala.ast.Definition.Statement
import dc10.scala.ctx.ErrorF

trait Applications[F[_]]:

  extension [T[_]] (function: F[Term.TypeLevel[T[__]]])
    @scala.annotation.targetName("app1T")
    def apply[A](args: F[Term.TypeLevel[A]]): F[Term.TypeLevel[T[A]]]

  extension [T[_,_]] (tfunction: F[Term.TypeLevel.Lam2[T]])
    @scala.annotation.targetName("app2T")
    def apply[A, B](fta: F[Term.TypeLevel[A]])(ftb: F[Term.TypeLevel[B]]): F[Term.TypeLevel[T[A, B]]]

  extension [A, B] (function: F[Term.ValueLevel[A => B]])
    @scala.annotation.targetName("app1V")
    def apply(args: F[Term.ValueLevel[A]]): F[Term.ValueLevel[B]]

object Applications:

  trait Mixins extends Applications[[A] =>> StateT[ErrorF, List[Statement], A]]:

    extension [T[_]] (tfunction: StateT[ErrorF, List[Statement], Term.TypeLevel[T[__]]])
      @scala.annotation.targetName("app1T")
      def apply[A](targs: StateT[ErrorF, List[Statement], Term.TypeLevel[A]]): StateT[ErrorF, List[Statement], Term.TypeLevel[T[A]]] =
        for
          f <- tfunction
          a <- targs
        yield Term.TypeLevel.App1[T, A](f, a)

    extension [T[_,_]] (tfunction: StateT[ErrorF, List[Statement], Term.TypeLevel.Lam2[T]])
      @scala.annotation.targetName("app2T")
      def apply[A, B](fta: StateT[ErrorF, List[Statement], Term.TypeLevel[A]])(ftb: StateT[ErrorF, List[Statement], Term.TypeLevel[B]]): StateT[ErrorF, List[Statement], Term.TypeLevel[T[A, B]]] =
        for
          f <- tfunction
          a <- fta
          b <- ftb
        yield Term.TypeLevel.App2[T, A, B](f, a, b)

    extension [A, B] (function: StateT[ErrorF, List[Statement], Term.ValueLevel[A => B]])
      @scala.annotation.targetName("app1V")
      def apply(args: StateT[ErrorF, List[Statement], Term.ValueLevel[A]]): StateT[ErrorF, List[Statement], Term.ValueLevel[B]] =
        for
          f <- function
          a <- args
        yield Term.ValueLevel.App1[A, B](f, a)