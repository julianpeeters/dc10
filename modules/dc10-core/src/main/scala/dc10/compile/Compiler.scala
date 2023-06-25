package dc10.compile

import cats.{FlatMap, Functor}
import cats.data.StateT
import cats.kernel.Monoid
import dc10.config.LangConfig
import dc10.render.LangRenderer
import dc10.schema.Binding
import dc10.schema.definition.{FileDef, Statement}

trait Compiler[F[_], V, A]:
  def generate(res: List[A], cfg: LangConfig[V]): F[List[VirtualFile]]

object Compiler:

  sealed trait Error

  type ErrorF[A] = Either[List[Compiler.Error], A]

  given compiler[V] (
    using
      C: LangConfig[V],
      D: LangRenderer[V],
  ): Compiler[ErrorF, V, FileDef] =
    new Compiler[ErrorF, V, FileDef]:
      def generate(
        res: List[FileDef],
        cfg: LangConfig[V]
      ): Either[List[Compiler.Error], List[VirtualFile]] =
        Right(
          res.map(fileDef =>
            VirtualFile(
              fileDef.file.path,
              fileDef.file.contents.map(D.render).mkString)))

  extension [L, A](
    ast: StateT[[X] =>> Either[List[Compiler.Error], X], L, A]
  )(using Monoid[L])
    def compile: Either[List[Compiler.Error], L] =
      ast.runEmptyS
  
  extension [F[_]: Functor, A] (res: F[List[Statement[Binding]]])
    def toString[V](
      using
        E: LangRenderer[V]
    ): F[String] =
      Functor[F].map(res)(r => r.map(E.render).mkString("\n"))

  extension [F[_]: FlatMap, A] (res: F[List[A]])
    def toVirtualFile[V](
      using
        C: Compiler[F, V, A],
        D: LangConfig[V]
    ): F[List[VirtualFile]] =
      FlatMap[F].flatMap(res)(r => C.generate(r, D))