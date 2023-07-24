package dc10.scala.ctx

import cats.{Applicative, Functor}
import dc10.compiler.CodeGenerator.VirtualAst
import dc10.scala.ast.Statement
import dc10.scala.error.CompileError

type ErrorF[A] = Either[List[CompileError], A]

extension [F[_]: Applicative: Functor](ctx: List[Statement])
  def ext(s: Statement): F[List[Statement]] =
    Functor[F].map(namecheck(s))(ctx :+ _)
  def namecheck(s: Statement): F[Statement] =
    // TODO
    Applicative[F].pure(s)

extension [F[_]: Applicative: Functor] (ctx: List[VirtualAst[Statement]])
  def ext(s: VirtualAst[Statement]): F[List[VirtualAst[Statement]]] =
    Functor[F].map(namecheck(s))(ctx :+ _)
  def namecheck(s: VirtualAst[Statement]): F[VirtualAst[Statement]] =
    // TODO
    Applicative[F].pure(s)