package dc10.scala.ctx.predef

import cats.implicits.*
import cats.data.StateT
import dc10.scala.ast.Binding
import dc10.scala.ast.Binding.Term
import dc10.scala.ast.Definition.Statement
import dc10.scala.ctx.ErrorF

trait Functions[F[_]]:

  extension [A, B] (domain: F[Term.TypeLevel[A]])
    @scala.annotation.targetName("fun1T")
    def ==>(codomain: F[Term.TypeLevel[B]]): F[Term.TypeLevel[A => B]]

  extension [A, B] (fa: F[Term.ValueLevel.Var.UserDefinedValue[A]])
    @scala.annotation.targetName("fun1V")
    def ==>(f: Term.ValueLevel[A] => F[Term.ValueLevel[B]]): F[Term.ValueLevel[A => B]]

object Functions:

  trait Mixins extends Functions[[A] =>> StateT[ErrorF, List[Statement], A]]:
 
    extension [A, B] (domain: StateT[ErrorF, List[Statement], Term.TypeLevel[A]])
      @scala.annotation.targetName("fun1T")
      def ==>(
        codomain: StateT[ErrorF, List[Statement], Term.TypeLevel[B]]
      ): StateT[ErrorF, List[Statement], Term.TypeLevel[A => B]] =
        for
          a <- domain
          b <- codomain
          v <- StateT.pure(Term.TypeLevel.App2(Term.TypeLevel.Var.Function1Type, a, b))
        yield v

    extension [A, B] (fa: StateT[ErrorF, List[Statement], Term.ValueLevel.Var.UserDefinedValue[A]])
      @scala.annotation.targetName("fun1V")
      def ==>(
        f: Term.ValueLevel[A] => StateT[ErrorF, List[Statement], Term.ValueLevel[B]]
      ): StateT[ErrorF, List[Statement], Term.ValueLevel[A => B]] =
        for
          a <- StateT.liftF(fa.runEmptyA)
          b <- f(a)
          v <- StateT.pure(Term.ValueLevel.Lam1(a, b))
        yield v

        
