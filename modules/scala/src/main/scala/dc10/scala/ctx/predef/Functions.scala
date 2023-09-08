package dc10.scala.ctx.predef

import cats.implicits.*
import cats.data.StateT
import dc10.scala.ast.Binding.Term
import dc10.scala.ast.Binding.Term.{TypeLevel, ValueLevel}
import dc10.scala.ast.Binding.Term.ValueLevel.Var.UserDefinedValue
import dc10.scala.ast.Statement
import dc10.scala.ast.Statement.Expr
import dc10.scala.ctx.ErrorF

trait Functions[F[_]]:

  extension [A, B] (domain: F[Expr[TypeLevel, A]])
    @scala.annotation.targetName("fun1T")
    def ==>(codomain: F[Expr[TypeLevel, B]]): F[Expr[TypeLevel, A => B]]

  extension [A, B] (fa: F[Expr[UserDefinedValue, A]])
    @scala.annotation.targetName("fun1V")
    def ==>(f: Expr[ValueLevel, A] => F[Expr[ValueLevel, B]]): F[Expr[ValueLevel, A => B]]

object Functions:

  trait Mixins extends Functions[[A] =>> StateT[ErrorF, List[Statement], A]]:
 
    extension [A, B] (domain: StateT[ErrorF, List[Statement], Expr[TypeLevel, A]])
      @scala.annotation.targetName("fun1T")
      def ==>(
        codomain: StateT[ErrorF, List[Statement], Expr[TypeLevel, B]]
      ): StateT[ErrorF, List[Statement], Expr[TypeLevel, A => B]] =
        for
          a <- domain
          b <- codomain
          v <- StateT.pure(Expr.BuiltInType(Term.TypeLevel.App2(Expr.BuiltInType(Term.TypeLevel.Var.Function1Type), a, b)))
        yield v

    extension [A, B] (fa: StateT[ErrorF, List[Statement], Expr[UserDefinedValue, A]])
      @scala.annotation.targetName("fun1V")
      def ==>(
        f: Expr[ValueLevel, A] => StateT[ErrorF, List[Statement], Expr[ValueLevel, B]]
      ): StateT[ErrorF, List[Statement], Expr[ValueLevel, A => B]] =
        for
          a <- StateT.liftF(fa.runEmptyA)
          b <- f(a)
          v <- StateT.pure(Term.ValueLevel.Lam1(a, b))
        yield Expr.BuiltInValue(v)

        
