# dc10
***D**efinitional* ***C**ompiler* tools
 - [`dc10-core`](#dc10-core): core models and abstractions for defining a language implementation
 - [`dc10-io`](#dc10-io): fs2 integration for evaluating metaprograms into source files


<details><summary>examples</summary>
     
  - [`dc10-scala`](https://github.com/julianpeeters/dc10-scala): AST and dsl for defining and rendering Scala programs

</details>

-----

### `dc10-core`
 - Library for Scala 3 (JVM only)
 - Bring your own AST

```scala
"com.julianpeeters" %% "dc10-core" % "0.3.0"
```

The `compile` package provides abstractions for implementation by a downstream
language library:

<details><summary>Compiler</summary>

```scala
package dc10.compile

trait Compiler[
  F[_],              // Error functor in ctx
  G[_],              // Output unit, e.g., List, Id, etc.
  E,                 // Error type
  A,                 // Code level, representing symbols introduced into ctx
  B                  // File level, representing source files with path and ast
]:

  type Ctx[_[_],_,_] // Monadic context, to build up ASTs and then compile them

  extension [C, D] (ast: Ctx[F, List[D], C])
    def compile: F[List[D]]

  extension (res: F[G[A]])
    def toString[V](using R: Renderer[V, E, G[A]]): String

  extension (res: F[G[A]])
    def toStringOrError[V](using R: Renderer[V, E, G[A]]): F[String]

  extension (res: F[G[B]])
    def toVirtualFile[V](using R: Renderer[V, E, G[A]]): F[List[VirtualFile]]
```
</details>

<details><summary>Renderer</summary>

```scala
package dc10.compile

trait Renderer[V, E, A]:
  def render(input: A): String
  def renderErrors(errors: List[E]): String
  def version: V
```
</details>

<details><summary>VirtualFile</summary>

```scala
package dc10.compile

import java.nio.file.Path

case class VirtualFile(path: Path, contents: String)
```
</details>

### `dc10-io`
 - Library for Scala 3 (JVM only)
 - Bring your own AST, compiler, and renderer implementations

```scala
"com.julianpeeters" %% "dc10-io" % "0.3.0"
```
The `io` package provides extension methods to write files using fs2:

<details><summary>FileWriter</summary>

```scala
extension [
  F[_]: Concurrent: Files,
  G[_]: Foldable,
  H[_],
  E,
  A,
  B
](res: G[H[B]])(using C: Compiler[G, H, E, A, B])
  def toFile[V](using R: Renderer[V, E, H[A]]): F[List[Path]] =
    C.toVirtualFile(res)
      .foldMapM(e => e.traverse(s => FileWriter[F].writeFile(s)))
```
</details>