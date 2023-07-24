package dc10.scala

import cats.data.StateT
import cats.kernel.Monoid
import dc10.compiler.{CodeGenerator, Compiler, Renderer}
import dc10.compiler.CodeGenerator.{VirtualAst, VirtualFile}
import dc10.scala.ast.{Binding, Statement}
import dc10.scala.error.CompileError

type Γ = List[Statement]
type ErrorF[A] = Either[List[CompileError], A]

object compiler extends Compiler[ErrorF]:

  type Ctx[F[_], L, A] = StateT[ErrorF, L, A]

  type Defn = Statement
  type Ent = Binding
  type Err = CompileError

  extension [L: Monoid, A] (ast: StateT[ErrorF, L, A])
    def compile: ErrorF[L] =
      ast.runEmptyS

  extension (res: Either[List[CompileError], Γ])
    def toString[V](using R: Renderer[V, CompileError, Statement]): String =
      res.fold(R.renderErrors, R.render)

  extension (res: Either[List[CompileError], Γ])
    def toStringOrError[V](using R: Renderer[V, CompileError, Statement]): Either[List[CompileError], String] =
      res.map(R.render)

  extension (res: Either[List[CompileError], List[VirtualAst[Statement]]])
    def toVirtualFile[V](using C: CodeGenerator[Statement]): Either[List[CompileError], List[VirtualFile]] =
      res.map(C.generate)

