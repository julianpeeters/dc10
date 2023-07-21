package dc10.compiler

import cats.FlatMap
import cats.data.StateT
import cats.kernel.Monoid
import dc10.renderer.Renderer
import dc10.schema.FileDef
import dc10.compiler.Compiler.VirtualFile

extension [F[_]: FlatMap, L: Monoid, A] (ast: StateT[F, L, A])
  def compile: F[L] =
    ast.runEmptyS

extension [F[_]: FlatMap, A](res: F[List[A]])
  def toString[V](using R: Renderer[F, V, A]): F[String] =
    FlatMap[F].flatMap(res)(R.render)

extension [F[_]: FlatMap, A] (res: F[List[FileDef[A]]])
  def toVirtualFile[V](using C: Compiler[F, A]): F[List[VirtualFile]] =
    FlatMap[F].flatMap(res)(C.generate)