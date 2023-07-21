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

  given compiler[V, A] (
    using
      E: Applicative[ErrorF],
      C: Config[V],
      D: Renderer[Compiler.ErrorF, V, A],
  ): Compiler[ErrorF, A] =
    new Compiler[ErrorF, A]:
      def generate(
        input: List[FileDef[A]],
      ): Compiler.ErrorF[List[VirtualFile]] =
          input.traverse(fileDef =>
            D.render(fileDef.contents).map(str =>
              VirtualFile(
                fileDef.path,
                str
              )
            )
          )