package dc10.ast.predef

import cats.FlatMap
import cats.implicits.*
import dc10.ast.Binding.Term

trait Variable[F[_]]:
  def declare[T](nme: String, tpe: F[Term.TypeLevel[T]]): F[Term.ValueLevel.Var.UserDefinedValue[T]]
  def define[T](nme: String, tpe: F[Term.TypeLevel[T]], impl: F[Term.ValueLevel[T]]): F[Term.ValueLevel.Var.UserDefinedValue[T]]

object Variable:

  def apply[F[_]: FlatMap]: Variable[F] =
    new Variable[F]:

      def declare[T](nme: String, tpe: F[Term.TypeLevel[T]]): F[Term.ValueLevel.Var.UserDefinedValue[T]] =
        for
          t <- tpe
        yield Term.ValueLevel.Var.UserDefinedValue(nme, t, None)

      def define[T](nme: String, tpe: F[Term.TypeLevel[T]], impl: F[Term.ValueLevel[T]]): F[Term.ValueLevel.Var.UserDefinedValue[T]] =
        for
          t <- tpe
          i <- impl
        yield Term.ValueLevel.Var.UserDefinedValue(nme, t, Some(i))
