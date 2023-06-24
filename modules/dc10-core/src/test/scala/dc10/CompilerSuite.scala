package dc10

import dc10.compile.Compiler
import dc10.schema.{CaseClass, File, Type, Value}
import dc10.schema.define.{CaseClassDef, FileDef, Statement, ValDef}
import java.nio.file.Path
import munit.FunSuite
// import dc10.render.given_Inspector_Type_Value

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
                    List(ValDef(Value("name", Type.STRING, None))),
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