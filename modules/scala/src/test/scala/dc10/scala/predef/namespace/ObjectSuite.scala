package dc10.scala.predef.namespace

import _root_.scala.language.implicitConversions

import munit.FunSuite

class ObjectSuite extends FunSuite:
  
  // schema
  import dc10.scala.dsl.{*, given}

  // compile
  import dc10.scala.compiler.{compile, toString}
  import dc10.scala.version.`3.3.1`

  test("obj def"):

    def ast = OBJECT("MyObject",
      for
        _ <- VAL("t", BOOLEAN, true)
        _ <- VAL("f", BOOLEAN, false)
      yield ()
    )
    
    val obtained: String =
      ast.compile.toString["scala-3.3.1"]
      
    val expected: String =
      """object MyObject:
        |
        |  val t: Boolean = true
        |  val f: Boolean = false""".stripMargin

    assertEquals(obtained, expected)
      