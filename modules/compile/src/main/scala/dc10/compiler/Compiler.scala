package dc10.compiler

import cats.Applicative
import cats.implicits.*
import dc10.renderer.Renderer
import dc10.schema.FileDef
import java.nio.file.Path

trait Compiler[F[_], A]:
  def generate(input: List[FileDef[A]]): F[List[Compiler.VirtualFile]]

object Compiler:
  
  type ErrorF[A] = Either[List[Compiler.Error], A]
  sealed trait Error

  case class VirtualFile(path: Path, contents: String)

  given compiler[F[_], V, A] (
    using
      E: Applicative[F],
      C: Config[V],
      D: Renderer[F, V, A],
  ): Compiler[F, A] =
    new Compiler[F, A]:
      def generate(
        input: List[FileDef[A]],
      ): F[List[VirtualFile]] =
          input.traverse(fileDef =>
            E.map(D.render(fileDef.contents))(str =>
              VirtualFile(
                fileDef.path,
                str
              )
            )
          )