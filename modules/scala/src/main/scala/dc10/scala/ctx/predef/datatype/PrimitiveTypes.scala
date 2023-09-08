package dc10.scala.ctx.predef.datatype

import cats.data.StateT
import dc10.scala.ast.Symbol.Term
import dc10.scala.ast.Symbol.Term.ValueLevel
import dc10.scala.ast.Statement
import dc10.scala.ast.Statement.Expr
import dc10.scala.ctx.ErrorF

trait PrimitiveTypes[F[_]]:

  def BOOLEAN: F[Expr[Term.TypeLevel, Boolean]]
  given bLit: Conversion[Boolean, F[Expr[ValueLevel, Boolean]]]
  
  def INT: F[Expr[Term.TypeLevel, Int]]
  given iLit: Conversion[Int, F[Expr[ValueLevel, Int]]]
  
  def STRING: F[Expr[Term.TypeLevel, String]]
  given sLit: Conversion[String, F[Expr[ValueLevel, String]]]
  
object PrimitiveTypes:

  trait Mixins extends PrimitiveTypes[[A] =>> StateT[ErrorF, List[Statement], A]]:

    def BOOLEAN: StateT[ErrorF, List[Statement], Expr[Term.TypeLevel, Boolean]] =
      StateT.pure(Expr.BuiltInType(Term.TypeLevel.Var.BooleanType))
      
    given bLit: Conversion[
      Boolean,
      StateT[ErrorF, List[Statement], Expr[ValueLevel, Boolean]]
    ] =
      v => StateT.pure(Expr.BuiltInValue(Term.ValueLevel.Var.BooleanLiteral(v)))

    def INT: StateT[ErrorF, List[Statement], Expr[Term.TypeLevel, Int]] =
      StateT.pure(Expr.BuiltInType(Term.TypeLevel.Var.IntType))

    given iLit: Conversion[
      Int,
      StateT[ErrorF, List[Statement], Expr[ValueLevel, Int]]
    ] =
      v => StateT.pure(Expr.BuiltInValue(Term.ValueLevel.Var.IntLiteral(v)))

    def STRING: StateT[ErrorF, List[Statement], Expr[Term.TypeLevel, String]] =
      StateT.pure(Expr.BuiltInType(Term.TypeLevel.Var.StringType))
    
    given sLit: Conversion[
      String,
      StateT[ErrorF, List[Statement], Expr[ValueLevel, String]]
    ] =
      v => StateT.pure(Expr.BuiltInValue(Term.ValueLevel.Var.StringLiteral(v)))