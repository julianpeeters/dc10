package dc10.scala.predef

import _root_.scala.language.implicitConversions
import cats.implicits.*
import dc10.compiler.{Compiler, compile, toString}
import dc10.scala.dsl.{*, given}
import dc10.scala.version.`3.3.0`

import munit.FunSuite
import dc10.scala.dsl

class FunctionsSuite extends FunSuite:

  
  test("val dec"):

    def ast =
      for
        _ <- VAL("f1", INT ==> STRING)
      yield ()
    
    val obtained: Either[List[Compiler.Error], String] =
      ast.compile.toString["scala-3.3.0"]
      
    val expected: Either[List[Compiler.Error], String] =
      Right("""val f1: Int => String""".stripMargin
      )
      
    assertEquals(obtained, expected)

  test("val def"):
    
    def ast =
      for
        f <- VAL("f1", STRING ==> STRING)(
          VAL("input", STRING) ==> (s => s)
        )
        b <- VAL("b", STRING)(f("hello"))
        _ <- VAL("c", STRING)(f(b))
      yield ()
    
    val obtained: Either[List[Compiler.Error], String] =
      ast.compile.toString["scala-3.3.0"]
      
    val expected: Either[List[Compiler.Error], String] =
      Right("""|val f1: String => String = input => input
               |val b: String = f1("hello")
               |val c: String = f1(b)""".stripMargin
      )
      
    assertEquals(obtained, expected)