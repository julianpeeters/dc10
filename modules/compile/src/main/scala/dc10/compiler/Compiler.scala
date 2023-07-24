package dc10.compiler

import cats.kernel.Monoid
import dc10.renderer.Renderer
import dc10.schema.FileSchema

trait Compiler[F[_]]:

  type Ctx[_[_], _, _]
  type Defn[_]
  type Ent
  type Err
  
  extension [L: Monoid, A] (ast: Ctx[F, L, A])
    def compile: F[L]

  extension (res: F[List[Defn[Ent]]])
    def toString[V](using R: Renderer[V, Err, Defn[Ent]]): String

  extension (res: F[List[Defn[Ent]]])
    def toStringOrError[V](using R: Renderer[V, Err, Defn[Ent]]): F[String]

  extension (res: F[List[FileSchema[Defn[Ent]]]])
    def toVirtualFile[V](using C: CodeGenerator[Defn[Ent]]): F[List[CodeGenerator.VirtualFile]]

