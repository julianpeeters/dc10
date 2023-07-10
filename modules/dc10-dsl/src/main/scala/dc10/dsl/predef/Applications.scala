package dc10.dsl.predef

import cats.implicits.*
import cats.data.StateT
import dc10.ast.Binding.Term
import dc10.ast.Binding.Term.TypeLevel.__
import dc10.compile.Compiler.{Γ, ErrorF}

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

  trait Mixins extends Applications[[A] =>> StateT[ErrorF, Γ, A]]:

    extension [T[_]] (tfunction: StateT[ErrorF, Γ, Term.TypeLevel[T[__]]])
      @scala.annotation.targetName("app1T")
      def apply[A](targs: StateT[ErrorF, Γ, Term.TypeLevel[A]]): StateT[ErrorF, Γ, Term.TypeLevel[T[A]]] =
        for
          f <- tfunction
          a <- targs
        yield Term.TypeLevel.App1[T, A](f, a)

    extension [T[_,_]] (tfunction: StateT[ErrorF, Γ, Term.TypeLevel.Lam2[T]])
      @scala.annotation.targetName("app2T")
      def apply[A, B](fta: StateT[ErrorF, Γ, Term.TypeLevel[A]])(ftb: StateT[ErrorF, Γ, Term.TypeLevel[B]]): StateT[ErrorF, Γ, Term.TypeLevel[T[A, B]]] =
        for
          f <- tfunction
          a <- fta
          b <- ftb
        yield Term.TypeLevel.App2[T, A, B](f, a, b)

    extension [A, B] (function: StateT[ErrorF, Γ, Term.ValueLevel[A => B]])
      @scala.annotation.targetName("app1V")
      def apply(args: StateT[ErrorF, Γ, Term.ValueLevel[A]]): StateT[ErrorF, Γ, Term.ValueLevel[B]] =
        for
          f <- function
          a <- args
        yield Term.ValueLevel.App1[A, B](f, a)