package dc10.scala.ast

import java.nio.file.Path
import dc10.scala.ast.Binding.Term.{TypeLevel, ValueLevel}
import dc10.scala.ast.Statement.Expr

sealed trait Binding

object Binding:

  // Templates ////////////////////////////////////////////////////////////////
  sealed abstract class CaseClass[T] extends Binding:
    type Tpe = T
    def nme: String
    def tpe: Expr[TypeLevel, T]
    def fields: List[Statement.ValDef]
    def body: List[Statement]

  object CaseClass:
    def apply[T](
      n: String,
      fs: List[Statement.ValDef],
    ): CaseClass[T] =
      new CaseClass[T]:
        type Tpe = T
        def nme = n
        def tpe: Expr[TypeLevel, T] = Expr.UserType(Term.TypeLevel.Var.UserDefinedType[T](n, None))
        def fields = fs
        def body = Nil

  // Package //////////////////////////////////////////////////////////////////
  sealed abstract class Package extends Binding

  object Package:

    extension (pkg: Package)
      def getPath: Path =
        pkg match
          case Basic(nme, nst) => Path.of(nme).resolve(nst.pkg.getPath)
          case Empty(ms) => Path.of("")

      def addMember(stmt: Statement): Package =
        pkg match
          case Basic(nme, nst) => nst.pkg.addMember(stmt)
          case Empty(ms) => Empty(ms :+ stmt)

    case class Basic(
      nme: String,
      nst: Statement.PackageDef
    ) extends Package

    case class Empty(
      ms : List[Statement]
    ) extends Package

  // Term ///////////////////////////////////////////////////////////////
  sealed abstract class Term extends Binding

  object Term:

    sealed trait TypeLevel[T] extends Term
    object TypeLevel:
      type __
      case class App1[T[_], A](tfun: Expr[TypeLevel, T[__]], targ: Expr[TypeLevel, A]) extends TypeLevel[T[A]]
      case class App2[T[_,_], A, B](tfun: Expr[TypeLevel, T[__,__]], ta: Expr[TypeLevel, A], tb: Expr[TypeLevel, B]) extends TypeLevel[T[A, B]]
      sealed trait Lam1[F[_]] extends TypeLevel[F[__]]
      sealed trait Lam2[F[_,_]] extends TypeLevel[F[__,__]]
      sealed abstract class Var[T] extends TypeLevel[T]
      object Var:
        case object BooleanType extends Var[Boolean]
        case object IntType extends Var[Int]
        case object StringType extends Var[String]
        case object Function1Type extends Var[__ => __]
        case object ListType extends Var[List[__]]
        case class UserDefinedType[T](nme: String, impl: Option[Expr[TypeLevel, T]]) extends Var[T]
        
    sealed trait ValueLevel[T] extends Term
    object ValueLevel:
      case class App1[A, B](fun: Expr[ValueLevel, A => B], arg: Expr[ValueLevel, A]) extends Term.ValueLevel[B]
      case class AppCtor1[T, A](tpe: Expr[TypeLevel, T], arg: Expr[ValueLevel, A]) extends Term.ValueLevel[T]
      case class AppVargs[A, B](fun: Expr[ValueLevel, List[A] => B], vargs: Expr[ValueLevel, A]*) extends Term.ValueLevel[B]
      case class Lam1[A, B](a: Expr[ValueLevel.Var.UserDefinedValue, A], b: Expr[ValueLevel, B]) extends Term.ValueLevel[A => B]
      sealed abstract class Var[T] extends Term.ValueLevel[T]
      object Var:
        case class BooleanLiteral(b: Boolean) extends Var[Boolean]
        case class IntLiteral(i: Int) extends Var[Int]
        case class StringLiteral(s: String) extends Var[String]
        case class ListCtor[A]() extends Var[List[A] => List[A]]
        case class UserDefinedValue[T](nme: String, tpe: Expr[TypeLevel, T], impl: Option[Expr[ValueLevel, T]]) extends Var[T]