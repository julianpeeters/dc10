package dc10.scala.ctx

import cats.{Applicative, Functor}
import dc10.scala.ast.Statement
import dc10.scala.error.CompileError
import dc10.scala.file.ScalaFile

type ErrorF[A] = Either[List[CompileError], A]

extension [F[_]: Applicative: Functor](ctx: List[Statement])
  def ext(s: Statement): F[List[Statement]] =
    Functor[F].map(namecheck(s))(ctx :+ _)
  def namecheck(s: Statement): F[Statement] =
    // TODO
    Applicative[F].pure(s)

extension [F[_]: Applicative: Functor] (ctx: List[ScalaFile])
  def ext(s: ScalaFile): F[List[ScalaFile]] =
    Functor[F].map(namecheck(s))(ctx :+ _)
  def namecheck(s: ScalaFile): F[ScalaFile] =
    // TODO
    Applicative[F].pure(s)