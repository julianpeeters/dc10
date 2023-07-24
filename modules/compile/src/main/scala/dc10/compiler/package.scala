package dc10.compiler

import cats.{Bifoldable, FlatMap, Functor}
// import cats.{FlatMap, Functor}
import cats.data.StateT
import cats.kernel.Monoid
import dc10.compiler.CodeGenerator.VirtualFile
import dc10.renderer.Renderer
import dc10.schema.FileSchema

extension [F[_]: FlatMap, L: Monoid, A] (ast: StateT[F, L, A])
  def compile: F[L] =
    ast.runEmptyS

extension [F[_,_]: Bifoldable, E, A](res: F[E, A])
  def toString[V](using R: Renderer[V, E, A]): String =
    Bifoldable[F].bifoldMap(res)(R.renderError, R.render)
    
extension [F[_]: Functor, E, A](res: F[A])
  def toStringOrError[V](using R: Renderer[V, E, A]): F[String] =
    Functor[F].map(res)(R.render)

extension [F[_]: Functor, A] (res: F[List[FileSchema[A]]])
  def toVirtualFile[V](using C: CodeGenerator[A]): F[List[VirtualFile]] =
    Functor[F].map(res)(C.generate)


// trait Compiler[F[_,_], A]:
//   type Ent
//   type Err
//   type Defn[_]
//   extension (res: F[Err, Defn[Ent]])
//     def toString[V]: String