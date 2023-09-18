package dc10.scala.predef

import cats.implicits.*
import cats.data.StateT
import cats.Eval
import cats.free.Cofree
import dc10.scala.ast.Statement
import dc10.scala.ast.Statement.{TypeExpr, ValueExpr}
import dc10.scala.ast.Symbol.Term
import dc10.scala.ast.Symbol.Term.{Type, Value}
import dc10.scala.error.ErrorF

trait Functions[F[_]]:

  extension [A, B] (domain: F[TypeExpr[A]])
    @scala.annotation.targetName("fun1T")
    def ==>(codomain: F[TypeExpr[B]]): F[TypeExpr[A => B]]

  extension [A, B] (fa: F[ValueExpr[A]])
    @scala.annotation.targetName("fun1V")
    def ==>(f: ValueExpr[A] => F[ValueExpr[B]]): F[ValueExpr[A => B]]

object Functions:

  trait Mixins extends Functions[[A] =>> StateT[ErrorF, List[Statement], A]]:
 
    extension [A, B] (domain: StateT[ErrorF, List[Statement], TypeExpr[A]])
      @scala.annotation.targetName("fun1T")
      def ==>(
        codomain: StateT[ErrorF, List[Statement], TypeExpr[B]]
      ): StateT[ErrorF, List[Statement], TypeExpr[A => B]] =
        for
          a <- domain
          b <- codomain
          v <- StateT.pure[ErrorF, List[Statement], Type[A => B]](Cofree((), Eval.now(Term.TypeLevel.App2(None, Cofree((), Eval.now(Term.TypeLevel.Var.Function1Type(None))), a.tpe, b.tpe))))
        yield TypeExpr(v)

    extension [A, B] (fa: StateT[ErrorF, List[Statement], ValueExpr[A]])
      @scala.annotation.targetName("fun1V")
      def ==>(
        f: ValueExpr[A] => StateT[ErrorF, List[Statement], ValueExpr[B]]
      ): StateT[ErrorF, List[Statement], ValueExpr[A => B]] =
        for
          a <- StateT.liftF(fa.runEmptyA)
          b <- f(a)
          v <- StateT.pure[ErrorF, List[Statement], Value[A => B]](Cofree((), Eval.now(Term.ValueLevel.Lam1(None, a.value, b.value))))
        yield ValueExpr(v)

        
