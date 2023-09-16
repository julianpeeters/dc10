package dc10.scala

import cats.data.StateT
import cats.kernel.Monoid
import dc10.compile.{Compiler, Renderer}
import dc10.compile.VirtualFile
import dc10.scala.ast.Statement
import dc10.scala.error.{CompileError, ErrorF}
import dc10.scala.file.ScalaFile

implicit object compiler extends Compiler[ErrorF, List]:

  type Ctx[F[_], L, A] = StateT[F, L, A]
  type Def = Statement
  type Err = CompileError
  type Fil = ScalaFile

  extension [L: Monoid, A] (ast: StateT[ErrorF, L, A])
    def compile: ErrorF[L] =
      ast.runEmptyS

  extension (res: ErrorF[List[Statement]])
    def toString[V](
      using R: Renderer[V, CompileError, List[Statement]]
    ): String =
      res.fold(R.renderErrors, R.render)

  extension (res: ErrorF[List[Statement]])
    def toStringOrError[V](
      using R: Renderer[V, CompileError, List[Statement]]
    ): ErrorF[String] =
      res.map(R.render)

  extension (res: ErrorF[List[ScalaFile]])
    def toVirtualFile[V](
      using R: Renderer[V, CompileError, List[Statement]]
    ): ErrorF[List[VirtualFile]] =
      for
        fds <- res
      yield fds.map(fileDef =>
          VirtualFile(
            fileDef.path,
            R.render(fileDef.contents)
          )
        )