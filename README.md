# dc10
***D**efinitional* ***C**ompiler* tools
 - [`dc10-core`](#dc10-core): core models and abstractions for defining a language implementation
 - [`dc10-io`](#dc10-io): fs2 integration for evaluating metaprograms into source files


#### Examples
     
  - [`dc10-scala`](https://github.com/julianpeeters/dc10-scala): AST and dsl for defining and rendering Scala programs

</details>

-----

### `dc10-core`
 - Library for Scala 3 (JS, JVM, and Native platforms)
 - Bring your own AST, implement a `Renderer`

```scala
"com.julianpeeters" %% "dc10-core" % "0.4.0"
```

The core lib provides a compiler implementation and a renderer for
implementation by a downstream language library:

<details><summary>Compiler</summary>

```scala
package dc10

import cats.data.StateT
import dc10.file.{File, VirtualFile}

trait Compiler[
  C, // Code represention
  D, // Lib dependency representation
  E, // Error representation
]:

  type Ctx[F[_], L, A] = StateT[F, L, A]            // Monadic ctx in which to build up a program
  type Err[A]          = Either[List[E], A]         // Error functor in ctx
  type Γ               = (Set[D], List[C])          // Code level log
  type Δ               = (Set[D], List[File[G, C]]) // File level log
  
  extension [A] (ast: Ctx[Err, Γ, A])
    @scala.annotation.targetName("compileCode")
    def compile: Err[List[C]]

  extension [A] (ast: Ctx[Err, Δ, A])
    @scala.annotation.targetName("compileFile")
    def compile: Err[List[File[G, C]]]

  extension (res: Err[List[C]])
    def toString[V](using R: Renderer[V, E, C]): String

  extension (res: Err[List[C]])
    def toStringOrError[V](using R: Renderer[V, E, C]): Err[String]

  extension (res: Err[List[File[G, C]]])
    def toVirtualFile[V](using R: Renderer[V, E, C]): Err[List[VirtualFile]]
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

<details><summary>File</summary>

```scala
package dc10

import java.nio.file.Path

case class File[A](path: Path, contents: List[A])
object File:

  extension [A] (file: File[A])
    def addParent(path: Path): File[A] =
      file.copy(path = path.resolve(file.path))

case class VirtualFile(path: Path, contents: String)
```
</details>

### `dc10-io`
 - Library for Scala 3 (JVM only)
 - Bring your own AST, implement a `Renderer`

```scala
"com.julianpeeters" %% "dc10-io" % "0.4.0"
```
The `io` package provides extension methods to write files using fs2:

<details><summary>FileWriter</summary>

```scala
package dc10.io

import cats.effect.Concurrent
import cats.syntax.all.given
import dc10.compile.{Compiler, Renderer}
import dc10.file.File
import fs2.io.file.{Files, Path}

extension [
  F[_]: Concurrent: Files,
  G[_],
  C,
  D,
  E,
](res: Either[List[E], List[File[G, C]]])(using C: Compiler[G, C, D, E])
  def toFile[V](using R: Renderer[V, E, List[C]]): F[List[Path]] =
    C.toVirtualFile(res)
      .foldMapM(e => e.traverse(s => FileWriter[F].writeFile(s)))
```
</details>