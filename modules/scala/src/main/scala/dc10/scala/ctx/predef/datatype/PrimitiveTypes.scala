package dc10.scala.ctx.predef.datatype

import cats.data.StateT
import cats.Eval
import cats.free.Cofree
import dc10.scala.ast.Statement
import dc10.scala.ast.Statement.{TypeExpr, ValueExpr}
import dc10.scala.ast.Symbol.Term
import dc10.scala.ast.Symbol.Term.ValueLevel
import dc10.scala.error.ErrorF

trait PrimitiveTypes[F[_]]:

  def BOOLEAN: F[TypeExpr[Boolean]]
  given bLit: Conversion[Boolean, F[ValueExpr[Boolean]]]
  
  def INT: F[TypeExpr[Int]]
  given iLit: Conversion[Int, F[ValueExpr[Int]]]
  
  def STRING: F[TypeExpr[String]]
  given sLit: Conversion[String, F[ValueExpr[String]]]
  
object PrimitiveTypes:

  trait Mixins extends PrimitiveTypes[[A] =>> StateT[ErrorF, List[Statement], A]]:

    def BOOLEAN: StateT[ErrorF, List[Statement], TypeExpr[Boolean]] =
      StateT.pure(TypeExpr(Term.TypeLevel.Var.BooleanType))
      
    given bLit: Conversion[
      Boolean,
      StateT[ErrorF, List[Statement], ValueExpr[Boolean]]
    ] =
      v => StateT.pure(ValueExpr(Cofree((), Eval.now(Term.ValueLevel.Var.BooleanLiteral(v)))))

    def INT: StateT[ErrorF, List[Statement], TypeExpr[Int]] =
      StateT.pure(TypeExpr(Term.TypeLevel.Var.IntType))

    given iLit: Conversion[
      Int,
      StateT[ErrorF, List[Statement], ValueExpr[Int]]
    ] =
      v => StateT.pure(ValueExpr(Cofree((), Eval.now(Term.ValueLevel.Var.IntLiteral(v)))))

    def STRING: StateT[ErrorF, List[Statement], TypeExpr[String]] =
      StateT.pure(TypeExpr(Term.TypeLevel.Var.StringType))
    
    given sLit: Conversion[
      String,
      StateT[ErrorF, List[Statement], ValueExpr[String]]
    ] =
      v => StateT.pure(ValueExpr(Cofree((), Eval.now(Term.ValueLevel.Var.StringLiteral(v)))))