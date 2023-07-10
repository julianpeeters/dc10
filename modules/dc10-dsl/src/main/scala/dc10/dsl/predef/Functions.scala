package dc10.dsl.predef

import cats.implicits.*
import cats.data.StateT
import dc10.ast.Binding.Term
import dc10.ast.predef.{Application, Function}
import dc10.compile.Compiler.{Γ, ErrorF}

trait Functions[F[_]]:

  extension [A, B] (domain: F[Term.TypeLevel[A]])
    @scala.annotation.targetName("fun1T")
    def ==>(codomain: F[Term.TypeLevel[B]]): F[Term.TypeLevel[A => B]]

  extension [A, B] (fa: F[Term.ValueLevel.Var.UserDefinedValue[A]])
    @scala.annotation.targetName("fun1V")
    def ==>(f: Term.ValueLevel[A] => F[Term.ValueLevel[B]]): F[Term.ValueLevel[A => B]]

object Functions:

  trait Mixins extends Functions[[A] =>> StateT[ErrorF, Γ, A]]:
 
    extension [A, B] (domain: StateT[ErrorF, Γ, Term.TypeLevel[A]])
      @scala.annotation.targetName("fun1T")
      def ==>(codomain: StateT[ErrorF, Γ, Term.TypeLevel[B]]): StateT[ErrorF, Γ, Term.TypeLevel[A => B]] =
        for
          a <- domain
          b <- codomain
          v <- StateT.liftF(Application[ErrorF].function1TypeApplication[A, B](a, b))
        yield v

    extension [A, B] (fa: StateT[ErrorF, Γ, Term.ValueLevel.Var.UserDefinedValue[A]])
      @scala.annotation.targetName("fun1V")
      def ==>(f: Term.ValueLevel[A] => StateT[ErrorF, Γ, Term.ValueLevel[B]]): StateT[ErrorF, Γ, Term.ValueLevel[A => B]] =
        for
          a <- fa
          b <- f(a)
          v <- StateT.liftF(Function[ErrorF].function1Value(a, b))
        yield v

        
