package dc10.compile

import cats.kernel.Monoid

trait Compiler[F[_]]:

  type Ctx[_[_], _, _]  // Monadic context, in which to build up ASTs and then compile them.
  type Defn             // Definition level, representing introduction into the context.
  type Ent              // Entity level, i.e., all sorts, to be expressed, defined, and referenced.
  type Err              // Error type, parameterizing the context and the evaluation results.
  type Src              // File level, representing a source file with path and ast.
  
  extension [L: Monoid, A] (ast: Ctx[F, L, A])
    def compile: F[L]

  extension (res: F[List[Defn]])
    def toString[V](using R: Renderer[V, Err, Defn]): String

  extension (res: F[List[Defn]])
    def toStringOrError[V](using R: Renderer[V, Err, Defn]): F[String]

  extension (res: F[List[Src]])
    def toVirtualFile[V](using R: Renderer[V, Err, Defn]): F[List[VirtualFile]]