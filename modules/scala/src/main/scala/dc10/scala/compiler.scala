package dc10.scala

import cats.data.StateT
import dc10.compile.{Compiler, Renderer, VirtualFile}
import dc10.scala.ast.{ScalaFile, Statement}
import dc10.scala.error.{CompileError, ErrorF}

implicit object compiler extends Compiler[
  ErrorF,
  List,
  List,
  CompileError,
  Statement,
  ScalaFile
]:

  type Ctx[F[_], L, A] = StateT[F, L, A]
  // type L

  extension [C, D] (ast: StateT[ErrorF, List[D], C])
    def compile: ErrorF[List[D]] =
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