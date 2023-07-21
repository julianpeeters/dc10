package dc10.scala.ctx.predef.datatype

import cats.data.StateT
import dc10.scala.ast.Binding
import dc10.scala.ast.Binding.Term
import dc10.scala.ast.Definition.Statement
import dc10.compiler.Compiler.ErrorF
import dc10.compiler.Compiler

trait PrimitiveTypes[F[_]]:

  def BOOLEAN: F[Term.TypeLevel[Boolean]]
  given bLit: Conversion[Boolean, F[Term.ValueLevel[Boolean]]]
  
  def INT: F[Term.TypeLevel[Int]]
  given iLit: Conversion[Int, F[Term.ValueLevel[Int]]]
  
  def STRING: F[Term.TypeLevel[String]]
  given sLit: Conversion[String, F[Term.ValueLevel[String]]]
  
object PrimitiveTypes:

  trait Mixins extends PrimitiveTypes[[A] =>> StateT[ErrorF, List[Statement], A]]:

    def BOOLEAN: StateT[ErrorF, List[Statement], Term.TypeLevel[Boolean]] =
      StateT.pure(Term.TypeLevel.Var.BooleanType)
      
    given bLit: Conversion[
      Boolean,
      StateT[ErrorF, List[Statement], Term.ValueLevel[Boolean]]
    ] =
      v => StateT.pure(Term.ValueLevel.Var.BooleanLiteral(v))

    def INT: StateT[ErrorF, List[Statement], Term.TypeLevel[Int]] =
      StateT.pure(Term.TypeLevel.Var.IntType)

    given iLit: Conversion[
      Int,
      StateT[ErrorF, List[Statement], Term.ValueLevel[Int]]
    ] =
      v => StateT.pure(Term.ValueLevel.Var.IntLiteral(v))

    def STRING: StateT[ErrorF, List[Statement], Term.TypeLevel[String]] =
      StateT.pure(Term.TypeLevel.Var.StringType)
    
    given sLit: Conversion[
      String,
      StateT[ErrorF, List[Statement], Term.ValueLevel[String]]
    ] =
      v => StateT.pure(Term.ValueLevel.Var.StringLiteral(v))