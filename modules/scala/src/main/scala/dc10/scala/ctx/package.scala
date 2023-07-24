package dc10.scala.ctx

import cats.{Applicative, Functor}
import dc10.scala.ast.{Binding, Statement}
import dc10.scala.error.CompileError
import dc10.schema.FileSchema

type ErrorF[A] = Either[List[CompileError], A]

extension [F[_]: Applicative: Functor](ctx: List[Statement[Binding]])
  def ext(s: Statement[Binding]): F[List[Statement[Binding]]] =
    Functor[F].map(namecheck(s))(ctx :+ _)
  def namecheck(s: Statement[Binding]): F[Statement[Binding]] =
    // TODO
    Applicative[F].pure(s)

extension [F[_]: Applicative: Functor] (ctx: List[FileSchema[Statement[Binding]]])
  def ext(s: FileSchema[Statement[Binding]]): F[List[FileSchema[Statement[Binding]]]] =
    Functor[F].map(namecheck(s))(ctx :+ _)
  def namecheck(s: FileSchema[Statement[Binding]]): F[FileSchema[Statement[Binding]]] =
    // TODO
    Applicative[F].pure(s)