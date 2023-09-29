# dc10
A ***D**efinitional* ***C**ompiler* for defining and generating Scala code.
 - `dc10-compile`: Core models and compiler abstractions
 - `dc10-io`: Evaluate metaprograms into source files
 - `dc10-scala`: AST and dsl for constructing metaprograms

### Add the dependencies
 - Libraries for Scala @SCALA@ (JVM only)

```
"com.julianpeeters" %% "dc10-compile" % "@VERSION@",
"com.julianpeeters" %% "dc10-io"      % "@VERSION@",
"com.julianpeeters" %% "dc10-scala"   % "@VERSION@",
```

### Usage

#### `dc10-scala`

Use the dsl to define Scala code:

```scala
val snippet: StateT[ErrorF, List[Statement], Unit] = 
  for
    s <- VAL("str", STRING, "hello, world")
    _ <- VAL("msg", STRING, s)
  yield ()
```
