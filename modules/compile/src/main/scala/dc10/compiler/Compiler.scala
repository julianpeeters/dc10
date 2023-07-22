package dc10.compiler

import cats.implicits.*
import dc10.renderer.Renderer
import dc10.schema.FileDef
import java.nio.file.Path

trait Compiler[A]:
  def generate(input: List[FileDef[A]]): List[Compiler.VirtualFile]

object Compiler:
  
  type ErrorF[A] = Either[List[Compiler.Error], A]
  sealed trait Error

  case class VirtualFile(path: Path, contents: String)

  given compilerA[V, A] (
    using
      C: Config[V],
      D: Renderer[V, A],
  ): Compiler[A] =
    new Compiler[A]:
      def generate(
        input: List[FileDef[A]],
      ): List[VirtualFile] =
          input.map(fileDef =>
            VirtualFile(
              fileDef.path,
              D.render(fileDef.contents)
            )
          )