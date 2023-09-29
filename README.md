# dc10
A ***D**efinitional* ***C**ompiler* for compiling models of programs into programs.
 - `dc10-compile`: Evaluate metaprograms into code strings
 - `dc10-io`: Evaluate metaprograms into source files
 - `dc10-scala`: AST and dsl for constructing metaprograms

### Installation
 - Libraries for Scala 3 (JVM only)

#### Add the dependencies, e.g.:
```
"com.julianpeeters" %% "dc10-compile" % "0.1.0-SNAPSHOT"
```

### Usage

#### `dc10-scala`

Use the dsl to define some Scala code:

```scala
val snippet: StateT[ErrorF, List[Statement], Unit] = 
  for
    s <- VAL("str", STRING, "hello, world")
    _ <- VAL("msg", STRING, s)
  yield ()
```
