package dc10.ast.predef

import cats.Applicative
import dc10.ast.Binding.Term

sealed trait Function[F[_]]:
  def function1Value[A, B](a: Term.ValueLevel[A], b: Term.ValueLevel[B]): F[Term.ValueLevel[A => B]]

object Function:

  def apply[F[_]: Applicative]: Function[F] =
    new Function[F]:

      def function1Value[A, B](a: Term.ValueLevel[A], b: Term.ValueLevel[B]): F[Term.ValueLevel[A => B]] =
        Applicative[F].pure(Term.ValueLevel.Lam1(a, b))