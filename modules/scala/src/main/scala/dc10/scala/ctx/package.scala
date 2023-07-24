package dc10.scala.ctx

import cats.{Applicative, Functor}
import dc10.scala.ast.Statement
import dc10.scala.error.CompileError
import dc10.schema.FileSchema

type ErrorF[A] = Either[List[CompileError], A]

extension [F[_]: Applicative: Functor](ctx: List[Statement])
  def ext(s: Statement): F[List[Statement]] =
    Functor[F].map(namecheck(s))(ctx :+ _)
  def namecheck(s: Statement): F[Statement] =
    // TODO
    Applicative[F].pure(s)

extension [F[_]: Applicative: Functor] (ctx: List[FileSchema[List[Statement]]])
  def ext(s: FileSchema[List[Statement]]): F[List[FileSchema[List[Statement]]]] =
    Functor[F].map(namecheck(s))(ctx :+ _)
  def namecheck(s: FileSchema[List[Statement]]): F[FileSchema[List[Statement]]] =
    // TODO
    Applicative[F].pure(s)