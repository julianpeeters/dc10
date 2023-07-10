package dc10.dsl.predef.datatype

import cats.data.StateT
import dc10.ast.Binding.Term
import dc10.ast.predef.datatype.Primitive
import dc10.compile.Compiler.{ErrorF, Γ}

trait PrimitiveTypes[F[_]]:

  def BOOLEAN: F[Term.TypeLevel[Boolean]]
  given bLit: Conversion[Boolean, F[Term.ValueLevel[Boolean]]]
  
  def INT: F[Term.TypeLevel[Int]]
  given iLit: Conversion[Int, F[Term.ValueLevel[Int]]]
  
  def STRING: F[Term.TypeLevel[String]]
  given sLit: Conversion[String, F[Term.ValueLevel[String]]]
  
object PrimitiveTypes:

  trait Mixins extends PrimitiveTypes[[A] =>> StateT[ErrorF, Γ, A]]:

    def BOOLEAN: StateT[ErrorF, Γ, Term.TypeLevel[Boolean]] =
      StateT.liftF(Primitive[ErrorF].booleanType)
      
    given bLit: Conversion[
      Boolean,
      StateT[ErrorF, Γ, Term.ValueLevel[Boolean]]
    ] =
      b => StateT.liftF(Primitive[ErrorF].booleanValue(b))

    def INT: StateT[ErrorF, Γ, Term.TypeLevel[Int]] =
      StateT.liftF(Primitive[ErrorF].intType)

    given iLit: Conversion[
      Int,
      StateT[ErrorF, Γ, Term.ValueLevel[Int]]
    ] =
      i => StateT.liftF(Primitive[ErrorF].intValue(i))

    def STRING: StateT[ErrorF, Γ, Term.TypeLevel[String]] =
      StateT.liftF(Primitive[ErrorF].stringType)
    
    given sLit: Conversion[
      String,
      StateT[ErrorF, Γ, Term.ValueLevel[String]]
    ] =
      i => StateT.liftF(Primitive[ErrorF].stringValue(i))