package dc10.scala.ctx

import cats.{Applicative, Functor}
import dc10.scala.ast.{ScalaFile, Statement}
import dc10.scala.ast.Statement.PackageDef

extension [F[_]: Applicative: Functor] (ctx: List[PackageDef])
  def ext(s: PackageDef): F[List[PackageDef]] =
    Functor[F].map(namecheck(s))(ctx :+ _)
  def namecheck(s: PackageDef): F[PackageDef] =
    // TODO
    Applicative[F].pure(s)

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