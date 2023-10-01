# dc10
A ***D**efinitional* ***C**ompiler* for generating Scala code.
 - [`dc10-core`](#dc10-core): Core models and abstractions for defining a language implementation
 - [`dc10-io`](#dc10-io): Fs2 integration for evaluating metaprograms into source files
 - [`dc10-scala`](#dc10-scala): AST and dsl for defining and rendering Scala programs

### Getting Started
 - Libraries for Scala @SCALA@ (JVM only)
 - Generates code for Scala @SCALA@

```scala
"com.julianpeeters" %% "dc10-<module>" % "@VERSION@"
```

### Usage

### `dc10-core`
The `compile` package provides abstractions for defining and rendering code:

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
The `io` package provides extension methods to write files using fs2:

```scala
_.toFile["scala-3.3.1"]
```

### `dc10-scala`

Use the dsl to define Scala code:

```scala mdoc:silent
import cats.data.StateT
import dc10.scala.ast.Statement
import dc10.scala.dsl.{*, given}
import dc10.scala.error.ErrorF
import scala.language.implicitConversions // for literals, e.g. "hello, world"

val snippet: StateT[ErrorF, List[Statement], Unit] = 
  for
    s <- VAL("str", STRING, "hello, world")
    _ <- VAL("msg", STRING, s)
  yield ()
```

Use the compiler impl to check and render code to `String` or `VirtualFile`:

```scala mdoc
import dc10.scala.compiler.{compile, toString}
import dc10.scala.version.`3.3.1`

val result: String = snippet.compile.toString["scala-3.3.1"]
```