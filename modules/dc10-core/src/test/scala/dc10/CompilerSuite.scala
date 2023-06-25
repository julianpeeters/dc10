package dc10

import dc10.compile.Compiler
import dc10.schema.{CaseClass, File, Type, Value}
import dc10.schema.definition.{FileDef, Statement}
import java.nio.file.Path
import munit.FunSuite
import org.tpolecat.sourcepos.SourcePos

class CompilerSuite extends FunSuite:

  test("toVirtualFile"):

    def ast(using sp: SourcePos): Either[List[Compiler.Error], List[FileDef]] =
      Right(
        List(
          FileDef(
            File(
              Path.of("Person.scala"),
              List(
                Statement.CaseClassDef(
                  CaseClass(
                    "Person",
                    List(Statement.ValDef(Value("name", Type.string, None), 0)(sp)),
                    List.empty
                  ),
                  0
                )(sp)
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