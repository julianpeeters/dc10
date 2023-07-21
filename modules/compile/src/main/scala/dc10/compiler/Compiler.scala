package dc10.compiler

import cats.Applicative
import cats.implicits.*
import dc10.schema.FileDef

trait Compiler[F[_], A]:
  def generate(input: List[FileDef[A]]): F[List[VirtualFile]]

object Compiler:
  
  type ErrorF[A] = Either[List[Compiler.Error], A]
  sealed trait Error

  given compiler[V, A] (
    using
      E: Applicative[ErrorF],
      C: Config[V],
      D: Renderer[V, A],
  ): Compiler[ErrorF, A] =
    new Compiler[ErrorF, A]:
      def generate(
        input: List[FileDef[A]],
      ): Compiler.ErrorF[List[VirtualFile]] =
          input.traverse(fileDef =>
            E.pure(
              VirtualFile(
                fileDef.path,
                D.render(fileDef.contents)
              )
            )
          )