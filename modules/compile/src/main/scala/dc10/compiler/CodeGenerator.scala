package dc10.compiler

import cats.implicits.*
import dc10.renderer.Renderer
import dc10.schema.FileSchema
import java.nio.file.Path

trait CodeGenerator[A]:
  def generate(input: List[FileSchema[A]]): List[CodeGenerator.VirtualFile]

object CodeGenerator:
  
  case class VirtualFile(path: Path, contents: String)

  given codeGenerator[V, E, A] (
    using
      C: Config[V],
      D: Renderer[V, E, A],
  ): CodeGenerator[A] =
    new CodeGenerator[A]:
      def generate(
        input: List[FileSchema[A]],
      ): List[VirtualFile] =
          input.map(fileDef =>
            VirtualFile(
              fileDef.path,
              D.render(fileDef.contents)
            )
          )