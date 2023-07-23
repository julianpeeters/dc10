package dc10.compiler

import cats.implicits.*
import dc10.renderer.Renderer
import dc10.schema.FileDef
import java.nio.file.Path

trait Compiler[A]:
  def generate(input: List[FileDef[A]]): List[Compiler.VirtualFile]

object Compiler:
  
  case class VirtualFile(path: Path, contents: String)

  given compilerA[V, E, A] (
    using
      C: Config[V],
      D: Renderer[V, E, A],
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