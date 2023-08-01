package dc10.compile

import cats.kernel.Monoid

trait Compiler[F[_], G[_]]:

  type Ctx[_[_], _, _]  // Monadic context, in which to build up ASTs and then compile them.
  type Defn             // Definition level, representing introduction into the context.
  type Err              // Error type, parameterizing the context and the compilation results.
  type Fil              // File level, representing a source file with path and ast.
  
  extension [L: Monoid, A] (ast: Ctx[F, L, A])
    def compile: F[L]

  extension (res: F[G[Defn]])
    def toString[V](using R: Renderer[V, Err, G[Defn]]): String

  extension (res: F[G[Defn]])
    def toStringOrError[V](using R: Renderer[V, Err, G[Defn]]): F[String]

  extension (res: F[List[Fil]])
    def toVirtualFile[V](using R: Renderer[V, Err, G[Defn]]): F[List[VirtualFile]]