package dc10.dsl

import dc10.compile.Compiler
import dc10.schema.Binding.Value
import munit.FunSuite
import scala.language.implicitConversions

class SchemaBuilderSuite extends FunSuite:

  val dsl = SchemaBuilder.dsl
  import dsl.{*, given}

  test("toString"):
    
    def ast =
      for
        _ <- VAL("greeting", STRING, "hello, world")
        _ <- VAL("farewell", STRING, "goodbye, world")
        _ <- CASECLASS("Person", VAL("name", STRING))
      yield ()
    
    val obtained: Either[List[Compiler.Error], String] =
      ast.compile.toString["scala-3.3.0"]
      
    val expected: Either[List[Compiler.Error], String] =
      Right("""|val greeting: String = "hello, world"
               |val farewell: String = "goodbye, world"
               |case class Person(val name: String)""".stripMargin
      )
      
    assertEquals(obtained, expected)

  test("refV"):
    
    def ast =
      for
        m <- VAL("message", STRING, "hello, world")
        _ <- VAL("greeting", STRING, m)
      yield ()
    
    val obtained: Either[List[Compiler.Error], String] =
      ast.compile.toString["scala-3.3.0"]
      
    val expected: Either[List[Compiler.Error], String] =
      Right("""|val message: String = "hello, world"
               |val greeting: String = message""".stripMargin
      )
      
    assertEquals(obtained, expected)