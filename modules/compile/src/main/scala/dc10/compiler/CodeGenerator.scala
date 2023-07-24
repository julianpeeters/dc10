package dc10.compiler

import cats.implicits.*
import dc10.compiler.CodeGenerator.{VirtualAst, VirtualFile}
import java.nio.file.Path

trait CodeGenerator[A]:
  def generate(input: List[VirtualAst[A]]): List[VirtualFile]

object CodeGenerator:

  case class VirtualAst[A](path: Path, contents: List[A])  
  case class VirtualFile(path: Path, contents: String)

  given codeGenerator[V, E, A] (
    using
      C: Config[V],
      D: Renderer[V, E, A],
  ): CodeGenerator[A] =
    new CodeGenerator[A]:
      def generate(
        input: List[VirtualAst[A]],
      ): List[VirtualFile] =
          input.map(fileDef =>
            VirtualFile(
              fileDef.path,
              D.render(fileDef.contents)
            )
          )