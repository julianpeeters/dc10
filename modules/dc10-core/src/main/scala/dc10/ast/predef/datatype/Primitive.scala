package dc10.ast.predef.datatype

import cats.Applicative
import dc10.ast.Binding.Term.{TypeLevel, ValueLevel}

sealed trait Primitive[F[_]]:
  def booleanType: F[TypeLevel[Boolean]]
  def booleanValue(b: Boolean): F[ValueLevel[Boolean]]
  def intType: F[TypeLevel[Int]]
  def intValue(i: Int): F[ValueLevel[Int]]
  def stringType: F[TypeLevel[String]]
  def stringValue(s: String): F[ValueLevel[String]]

object Primitive:

  def apply[F[_]: Applicative]: Primitive[F] =
    new Primitive[F]:
      
      def booleanType: F[TypeLevel[Boolean]] =
        Applicative[F].pure(TypeLevel.Var.BooleanType)

      def booleanValue(b: Boolean): F[ValueLevel[Boolean]] =
         Applicative[F].pure(ValueLevel.Var.BooleanLiteral(b))

      def intType: F[TypeLevel[Int]] =
        Applicative[F].pure(TypeLevel.Var.IntType)

      def intValue(i: Int): F[ValueLevel[Int]] =
         Applicative[F].pure(ValueLevel.Var.IntLiteral(i))

      def stringType: F[TypeLevel[String]] =
        Applicative[F].pure(TypeLevel.Var.StringType)

      def stringValue(s: String): F[ValueLevel[String]] =
         Applicative[F].pure(ValueLevel.Var.StringLiteral(s))

