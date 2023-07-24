package dc10.scala

import cats.data.StateT
import dc10.compiler.{CodeGenerator, Compiler}
import dc10.renderer.Renderer
import dc10.scala.ast.{Binding, Statement}
import dc10.scala.error.CompileError
import dc10.schema.FileSchema
import cats.kernel.Monoid

type Γ = List[Statement[Binding]]
type ErrorF[A] = Either[List[CompileError], A]

object compiler extends Compiler[ErrorF]:

  type Ctx[F[_], L, A] = StateT[ErrorF, L, A]
  type Defn[A] = Statement[A]
  type Ent = Binding
  type Err = CompileError

  extension [L: Monoid, A] (ast: StateT[ErrorF, L, A])
    def compile: ErrorF[L] =
      ast.runEmptyS

  extension (res: Either[List[CompileError], Γ])
    def toString[V](using R: Renderer[V, CompileError, Statement[Binding]]): String =
      res.fold(R.renderErrors, R.render)

  extension (res: Either[List[CompileError], Γ])
    def toStringOrError[V](using R: Renderer[V, CompileError, Statement[Binding]]): Either[List[CompileError], String] =
      res.map(R.render)

  extension (res: Either[List[CompileError], List[FileSchema[Statement[Binding]]]])
    def toVirtualFile[V](using C: CodeGenerator[Statement[Binding]]): Either[List[CompileError], List[CodeGenerator.VirtualFile]] =
      res.map(C.generate)

