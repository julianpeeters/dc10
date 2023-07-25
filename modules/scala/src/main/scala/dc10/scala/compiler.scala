package dc10.scala

import cats.data.StateT
import cats.kernel.Monoid
import dc10.compile.{Compiler, Renderer}
import dc10.compile.VirtualFile
import dc10.scala.ast.{Binding, Statement}
import dc10.scala.file.ScalaFile
import dc10.scala.error.CompileError

type ErrorF[A] = Either[List[CompileError], A]

implicit object compiler extends Compiler[ErrorF]:

  type Ctx[F[_], L, A] = StateT[F, L, A]
  type Defn = Statement
  type Ent = Binding
  type Err = CompileError
  type Src = ScalaFile

  extension [L: Monoid, A] (ast: StateT[ErrorF, L, A])
    def compile: ErrorF[L] =
      ast.runEmptyS

  extension (res: ErrorF[List[Statement]])
    def toString[V](
      using R: Renderer[V, CompileError, Statement]
    ): String =
      res.fold(R.renderErrors, R.render)

  extension (res: ErrorF[List[Statement]])
    def toStringOrError[V](
      using R: Renderer[V, CompileError, Statement]
    ): ErrorF[String] =
      res.map(R.render)

  extension (res: ErrorF[List[ScalaFile]])
    def toVirtualFile[V](
      using R: Renderer[V, CompileError, Statement]
    ): ErrorF[List[VirtualFile]] =
      for
        fds <- res
      yield fds.map(fileDef =>
          VirtualFile(
            fileDef.path,
            R.render(fileDef.contents)
          )
        )