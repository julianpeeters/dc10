package dc10.scala.ast

import cats.free.Cofree
import dc10.scala.ast.Symbol.Term.{TypeLevel, ValueLevel}
import java.nio.file.Path

sealed trait Symbol

object Symbol:

  // Templates ////////////////////////////////////////////////////////////////
  sealed abstract class CaseClass[T] extends Symbol:
    type Tpe = T
    def nme: String
    def tpe: Term.TypeLevel.Var.UserDefinedType[T]
    def fields: List[Statement.ValDef]
    def body: List[Statement]

  object CaseClass:
    def apply[T](
      q: Option[Long],
      n: String,
      fs: List[Statement.ValDef],
    ): CaseClass[T] =
      new CaseClass[T]:
        type Tpe = T
        def nme = n
        def tpe: Term.TypeLevel.Var.UserDefinedType[T] = Term.TypeLevel.Var.UserDefinedType[T](q, n, None)
        def fields = fs
        def body = Nil

  // Package //////////////////////////////////////////////////////////////////
  sealed abstract class Package extends Symbol

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

  // Term /////////////////////////////////////////////////////////////////////
  sealed abstract class Term extends Symbol:
    def qnt: Option[Long]

  object Term:

    sealed trait TypeLevel[T] extends Term
    object TypeLevel:
      type __
      case class App1[T[_], A](qnt: Option[Long], tfun: TypeLevel[T[__]], targ: TypeLevel[A]) extends TypeLevel[T[A]]
      case class App2[T[_,_], A, B](qnt: Option[Long], tfun: TypeLevel[T[__,__]], ta: TypeLevel[A], tb: TypeLevel[B]) extends TypeLevel[T[A, B]]
      sealed abstract class Var[T] extends TypeLevel[T]
      object Var:
        case class BooleanType(qnt: Option[Long]) extends Var[Boolean]
        case class IntType(qnt: Option[Long]) extends Var[Int]
        case class StringType(qnt: Option[Long]) extends Var[String]
        case class Function1Type(qnt: Option[Long]) extends Var[__ => __]
        case class ListType(qnt: Option[Long]) extends Var[List[__]]
        case class UserDefinedType[T](qnt: Option[Long], nme: String, impl: Option[TypeLevel[T]]) extends Var[T]
        
    type Value[T] = Cofree[[X] =>> ValueLevel[T, X], Unit]
    sealed trait ValueLevel[T, X] extends Term
    object ValueLevel:
      case class App1[A, B, X](qnt: Option[Long], fun: Value[A => B], arg: Value[A]) extends Term.ValueLevel[B, X]
      case class AppCtor1[T, A, X](qnt: Option[Long], tpe: TypeLevel[T], arg: Value[A]) extends Term.ValueLevel[T, X]
      case class AppVargs[A, B, X](qnt: Option[Long], fun: Value[List[A] => B], vargs: Value[A]*) extends Term.ValueLevel[B, X]
      case class Lam1[A, B, X](qnt: Option[Long], a: Value[A], b: Value[B]) extends Term.ValueLevel[A => B, X]
      sealed abstract class Var[T, X] extends Term.ValueLevel[T, X]
      object Var:
        case class BooleanLiteral[X](qnt: Option[Long], b: Boolean) extends Var[Boolean, X]
        case class IntLiteral[X](qnt: Option[Long], i: Int) extends Var[Int, X]
        case class StringLiteral[X](qnt: Option[Long], s: String) extends Var[String, X]
        case class ListCtor[A, X](qnt: Option[Long]) extends Var[List[A] => List[A], X]
        case class UserDefinedValue[T, X](qnt: Option[Long], nme: String, tpe: TypeLevel[T], impl: Option[Value[T]]) extends Var[T, X]