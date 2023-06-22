package dc10

import dc10.compile.Compiler
import dc10.schema.{CaseClass, File, Type, Value}
import dc10.schema.define.{FileDef, CaseClassDef, Statement, ValDef}
import java.nio.file.Path
import munit.FunSuite

class CompilerSuite extends FunSuite:

  test("toVirtualFile"):

    def ast: Either[List[Compiler.Error], List[FileDef]] =
      Right(
        List(
          FileDef(
            File(
              Path.of("Person.scala"),
              List(
                CaseClassDef(
                  CaseClass(
                    "Person",
                    List(ValDef(Value("name", Type.String, None))),
                    List.empty
                  )
                )
              )
            )
          )
        )
      )
    
    val obtained: List[String] =
      ast.toVirtualFile["scala-3.3.0"] match
        case Left(value) => value.map(_.toString)
        case Right(value) => value.map(_.contents)
      
    val expected: List[String] =
      List("case class Person(val name: String)")

    assertEquals(obtained, expected)