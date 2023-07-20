package dc10

import cats.{Functor, FlatMap}
import cats.data.StateT
import cats.kernel.Monoid
import dc10.compiler.{Compiler, Renderer}
import dc10.compiler.VirtualFile
import dc10.schema.FileDef

extension [F[_]: FlatMap, L: Monoid, A] (ast: StateT[F, L, A])
  def compile: F[L] =
    ast.runEmptyS

extension [F[_]: Functor, A](res: F[List[A]])
  def toString[V](using R: Renderer[V, A]): F[String] =
    Functor[F].map(res)(R.render)

extension [F[_]: FlatMap, A] (res: F[List[FileDef[A]]])
  def toVirtualFile[V](using C: Compiler[F, A]): F[List[VirtualFile]] =
    FlatMap[F].flatMap(res)(C.generate)