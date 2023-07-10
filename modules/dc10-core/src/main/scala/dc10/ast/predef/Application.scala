package dc10.ast.predef

import cats.Applicative
import dc10.ast.Binding.Term

sealed trait Application[F[_]]:
  def ctor1Application[T, A](tpe: Term.TypeLevel[T], arg: Term.ValueLevel[A]): F[Term.ValueLevel[T]]
  def function1ValueApplication[A, B](f: Term.ValueLevel[A => B], a: Term.ValueLevel[A]): F[Term.ValueLevel[B]]
  def function1TypeApplication[A, B](a: Term.TypeLevel[A], b: Term.TypeLevel[B]): F[Term.TypeLevel[A => B]]

object Application:

  def apply[F[_]: Applicative]: Application[F] =
    new Application[F]:

      def ctor1Application[T, A](tpe: Term.TypeLevel[T], arg: Term.ValueLevel[A]): F[Term.ValueLevel[T]] =
        Applicative[F].pure(Term.ValueLevel.AppCtor1(tpe, arg))

      def function1ValueApplication[A, B](f: Term.ValueLevel[A => B], a: Term.ValueLevel[A]): F[Term.ValueLevel[B]] =
        Applicative[F].pure(Term.ValueLevel.App1(f, a))

      def function1TypeApplication[A, B](a: Term.TypeLevel[A], b: Term.TypeLevel[B]): F[Term.TypeLevel[A => B]] =
       Applicative[F].pure(Term.TypeLevel.App2[Function1, A, B](Term.TypeLevel.Var.Function1Type, a, b))
