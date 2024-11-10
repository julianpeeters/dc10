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
 - Bring your own AST, implement a `Renderer`, then compile to `String` or `File`

```scala
"com.julianpeeters" %% "dc10-core" % "0.5.0"
```

The `dc10` package provides the following:

1. A compiler implementation
2. A file model, and a virtual file model
3. A renderer interface (to be implemented by a downstream language library)


### `dc10-io`
 - Library for Scala 3 (JVM only)
 - Bring your own AST, implement a `Renderer`

```scala
"com.julianpeeters" %% "dc10-io" % "0.5.0"
```
The `io` package provides the following:

1. extension methods to write files using fs2 (provided)