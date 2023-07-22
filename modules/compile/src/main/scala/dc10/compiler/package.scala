package dc10.compiler

import cats.{FlatMap, Functor}
import cats.data.StateT
import cats.kernel.Monoid
import dc10.compiler.Compiler.VirtualFile
import dc10.renderer.Renderer
import dc10.schema.FileDef

extension [F[_]: FlatMap, L: Monoid, A] (ast: StateT[F, L, A])
  def compile: F[L] =
    ast.runEmptyS

extension [F[_]: Functor, A](res: F[A])
  def toStrings[V](using R: Renderer[V, A]): F[String] =
    Functor[F].map(res)(R.render)

extension [F[_]: Functor, A] (res: F[List[FileDef[A]]])
  def toVirtualFile[V](using C: Compiler[A]): F[List[VirtualFile]] =
    Functor[F].map(res)(C.generate)