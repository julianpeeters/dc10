package dc10.compile

import cats.kernel.Monoid

trait Compiler[
  F[_],              // Error functor in ctx
  G[_],              // Output unit, e.g., List, Id, etc.
  E,                 // Error type
  A,                 // Code level, representing symbols introduced into ctx
  B                  // File level, representing a source file of path and ast
]:

  type Ctx[_[_],_,_] // Monadic context, to build up ASTs and then compile them

  extension [L: Monoid, A] (ast: Ctx[F, L, A])
    def compile: F[L]

  extension (res: F[G[A]])
    def toString[V](using R: Renderer[V, E, G[A]]): String

  extension (res: F[G[A]])
    def toStringOrError[V](using R: Renderer[V, E, G[A]]): F[String]

  extension (res: F[G[B]])
    def toVirtualFile[V](using R: Renderer[V, E, G[A]]): F[List[VirtualFile]]