package dc10.compile

import cats.{FlatMap, Functor}
import cats.data.StateT
import dc10.ast.Binding
import dc10.ast.Definition.{Statement, SourceFile}
import dc10.compile.render.Renderer

trait Compiler[F[_], V, A]:
  def codeGen(res: List[A], cfg: Config[V]): F[List[VirtualFile]]

object Compiler:

  sealed trait Error

  type ErrorF[A] = Either[List[Compiler.Error], A]
  type Γ = List[Statement[Binding]]

  given compiler[V] (
    using
      C: Config[V],
      D: Renderer[V],
  ): Compiler[ErrorF, V, SourceFile] =
    new Compiler[ErrorF, V, SourceFile]:
      def codeGen(
        res: List[SourceFile],
        cfg: Config[V]
      ): Compiler.ErrorF[List[VirtualFile]] =
        Right(
          res.map(fileDef =>
            VirtualFile(
              fileDef.file.path,
              fileDef.file.contents.map(D.renderStatement).mkString)))

  extension [A](ast: StateT[Compiler.ErrorF, Γ, A])
    def compile: Compiler.ErrorF[Γ] =
      ast.runEmptyS
  
  extension [F[_]: Functor, A] (res: F[Γ])
    def toString[V](
      using
        D: Renderer[V]
    ): F[String] =
      Functor[F].map(res)(r => r.map(D.renderStatement).mkString("\n"))

  extension [F[_]: FlatMap, A] (res: F[List[A]])
    def toVirtualFile[V](
      using
        C: Compiler[F, V, A],
        D: Config[V]
    ): F[List[VirtualFile]] =
      FlatMap[F].flatMap(res)(r => C.codeGen(r, D))
  
  extension (ctx: Γ)
    def ext(s: Statement[Binding]): Compiler.ErrorF[Γ] =
      namecheck(s).map(ctx :+ _)
    def namecheck(s: Statement[Binding]): Compiler.ErrorF[Statement[Binding]] =
      // TODO
      Right(s)