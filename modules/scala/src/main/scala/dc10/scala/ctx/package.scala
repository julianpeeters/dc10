package dc10.scala.ctx

import cats.{Applicative, Functor}
import dc10.scala.ast.Definition.Statement
import dc10.scala.error.CompileError
import dc10.schema.FileDef

type ErrorF[A] = Either[List[CompileError], A]

extension [F[_]: Applicative: Functor](ctx: List[Statement])
  def ext(s: Statement): F[List[Statement]] =
    Functor[F].map(namecheck(s))(ctx :+ _)
  def namecheck(s: Statement): F[Statement] =
    // TODO
    Applicative[F].pure(s)

extension [F[_]: Applicative: Functor] (ctx: List[FileDef[List[Statement]]])
  def ext(s: FileDef[List[Statement]]): F[List[FileDef[List[Statement]]]] =
    Functor[F].map(namecheck(s))(ctx :+ _)
  def namecheck(s: FileDef[List[Statement]]): F[FileDef[List[Statement]]] =
    // TODO
    Applicative[F].pure(s)