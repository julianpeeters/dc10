package dc10

import dc10.ast.Binding.{Term}
import dc10.compile.Compiler
import munit.FunSuite

class CompilerSuite extends FunSuite:

  test("toVirtualFile"):

    def ast: Either[List[Compiler.Error], Term.ValueLevel.Var.StringLiteral] =
      for
        v <- Right(Term.ValueLevel.Var.StringLiteral("hello"))
      yield v
        
    val obtained: String = ast match
        case Left(value) => value.toString
        case Right(value) => value.s

    val expected: String =
      "hello"

    assertEquals(obtained, expected)