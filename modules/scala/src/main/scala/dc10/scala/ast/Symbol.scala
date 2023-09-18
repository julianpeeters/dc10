package dc10.scala.ast

import cats.Eval
import cats.free.Cofree
import dc10.scala.ast.Symbol.Term.{TypeLevel, ValueLevel}
import java.nio.file.Path

sealed trait Symbol

object Symbol:

  // Templates ////////////////////////////////////////////////////////////////
  sealed abstract class CaseClass[T] extends Symbol:
    type Tpe = T
    def nme: String
    def tpe: Term.Type[T]
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
        def tpe: Term.Type[T] = Cofree((), Eval.now(Term.TypeLevel.Var.UserDefinedType(q, n, None)))
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

    type Type[T] = Cofree[[X] =>> TypeLevel[T, X], Unit]
    sealed trait TypeLevel[T, X] extends Term
    object TypeLevel:
      type __
      case class App1[T[_], A, X](qnt: Option[Long], tfun: Type[T[__]], targ: Type[A]) extends TypeLevel[T[A], X]
      case class App2[T[_,_], A, B, X](qnt: Option[Long], tfun: Type[T[__,__]], ta: Type[A], tb: Type[B]) extends TypeLevel[T[A, B], X]
      sealed abstract class Var[T, X] extends TypeLevel[T, X]
      object Var:
        case class BooleanType[X](qnt: Option[Long]) extends Var[Boolean, X]
        case class IntType[X](qnt: Option[Long]) extends Var[Int, X]
        case class StringType[X](qnt: Option[Long]) extends Var[String, X]
        case class Function1Type[X](qnt: Option[Long]) extends Var[__ => __, X]
        case class ListType[X](qnt: Option[Long]) extends Var[List[__], X]
        case class UserDefinedType[T, X](qnt: Option[Long], nme: String, impl: Option[Type[T]]) extends Var[T, X]
        
    type Value[T] = Cofree[[X] =>> ValueLevel[T, X], Unit]
    sealed trait ValueLevel[T, X] extends Term
    object ValueLevel:
      case class App1[A, B, X](qnt: Option[Long], fun: Value[A => B], arg: Value[A]) extends Term.ValueLevel[B, X]
      case class AppCtor1[T, A, X](qnt: Option[Long], tpe: Type[T], arg: Value[A]) extends Term.ValueLevel[T, X]
      case class AppVargs[A, B, X](qnt: Option[Long], fun: Value[List[A] => B], vargs: Value[A]*) extends Term.ValueLevel[B, X]
      case class Lam1[A, B, X](qnt: Option[Long], a: Value[A], b: Value[B]) extends Term.ValueLevel[A => B, X]
      sealed abstract class Var[T, X] extends Term.ValueLevel[T, X]
      object Var:
        case class BooleanLiteral[X](qnt: Option[Long], b: Boolean) extends Var[Boolean, X]
        case class IntLiteral[X](qnt: Option[Long], i: Int) extends Var[Int, X]
        case class StringLiteral[X](qnt: Option[Long], s: String) extends Var[String, X]
        case class ListCtor[A, X](qnt: Option[Long]) extends Var[List[A] => List[A], X]
        case class UserDefinedValue[T, X](qnt: Option[Long], nme: String, tpe: Type[T], impl: Option[Value[T]]) extends Var[T, X]