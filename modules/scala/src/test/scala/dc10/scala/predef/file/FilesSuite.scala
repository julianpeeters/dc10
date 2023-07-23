package dc10.scala.predef.file

import _root_.scala.language.implicitConversions

import munit.FunSuite
import dc10.scala.error.CompileError
import dc10.scala.dsl

class PrimitiveTypeSuite extends FunSuite:
  
  // schema
  import dc10.scala.dsl.* 

  // compile
  import dc10.compiler.{compile, toVirtualFile}
  import dc10.scala.version.`3.3.0`

  test("val dec"):

    def ast = FILE("test.scala",
      for
        _ <- VAL("t", BOOLEAN)
        _ <- VAL("f", BOOLEAN)
        _ <- VAL("age", INT)
        _ <- VAL("year", INT)
        _ <- VAL("greeting", STRING)
        _ <- VAL("farewell", STRING)
      yield ()
    )
    val obtained: Either[List[CompileError], List[String]] =
      ast.compile.toVirtualFile["scala-3.3.0"]
        .map(fs => fs.map(vf => vf.contents))
      
    val expected: Either[List[CompileError], List[String]] =
      Right(scala.List("""|val t: Boolean
                          |val f: Boolean
                          |val age: Int
                          |val year: Int
                          |val greeting: String
                          |val farewell: String""".stripMargin
      ))
      
    assertEquals(obtained, expected)

  