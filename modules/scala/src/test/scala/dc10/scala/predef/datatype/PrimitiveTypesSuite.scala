package dc10.scala.predef.datatype

import _root_.scala.language.implicitConversions
import dc10.compiler.Compiler

import munit.FunSuite
import dc10.scala.dsl

class PrimitiveTypeSuite extends FunSuite:
  
  // schema
  import dc10.scala.dsl.{*, given}

  // compile
  import dc10.{compile, toString}
  import dc10.scala.version.`3.3.0`

  test("val dec"):

    def ast =
      for
        _ <- VAL("t", BOOLEAN)
        _ <- VAL("f", BOOLEAN)
        _ <- VAL("age", INT)
        _ <- VAL("year", INT)
        _ <- VAL("greeting", STRING)
        _ <- VAL("farewell", STRING)
      yield ()
    
    val obtained: Either[List[Compiler.Error], String] =
      ast.compile.toString["scala-3.3.0"]
      
    val expected: Either[List[Compiler.Error], String] =
      Right("""|val t: Boolean
               |val f: Boolean
               |val age: Int
               |val year: Int
               |val greeting: String
               |val farewell: String""".stripMargin
      )
      
    assertEquals(obtained, expected)

  
  test("val def"):

    def ast =
      for
        _ <- VAL("t", BOOLEAN)(true)
        _ <- VAL("f", BOOLEAN)(false)
        _ <- VAL("age", INT)(101)
        _ <- VAL("year", INT)(2020)
        _ <- VAL("greeting", STRING)("hello, world")
        _ <- VAL("farewell", STRING)("goodbye, world")
      yield ()
    
    val obtained: Either[List[Compiler.Error], String] =
      ast.compile.toString["scala-3.3.0"]
      
    val expected: Either[List[Compiler.Error], String] =
      Right("""|val t: Boolean = true
               |val f: Boolean = false
               |val age: Int = 101
               |val year: Int = 2020
               |val greeting: String = "hello, world"
               |val farewell: String = "goodbye, world"""".stripMargin
      )
      
    assertEquals(obtained, expected)
