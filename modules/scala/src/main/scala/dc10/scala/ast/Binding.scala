package dc10.scala.ast

import cats.implicits.*
import dc10.scala.ast.Definition.Statement
import java.nio.file.Path

sealed trait Binding

object Binding:

  // Files ////////////////////////////////////////////////////////////////////
  sealed abstract class File extends Binding:
    def path: Path
    def contents: List[Statement]

  object File:
    def apply(p: Path, c: List[Statement]): File =
      new File:
        def path: Path = p
        def contents: List[Statement] = c

  // Templates ////////////////////////////////////////////////////////////////
  sealed abstract class CaseClass[T, A] extends Binding:
    type Tpe = T
    def nme: String
    def tpe: Term.TypeLevel[T]
    def fields: List[Statement.ValDef]
    def body: List[Statement]

  object CaseClass:
    def apply[T, A](
      n: String,
      fs: List[Statement.ValDef],
    ): CaseClass[T, A] =
      new CaseClass[T, A]:
        type Tpe = T
        def nme = n
        def tpe: Term.TypeLevel[T] = Term.TypeLevel.Var.UserDefinedType[T](n, None)
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
      case class App1[T[_], A](tfun: Term.TypeLevel[T[__]], targ: Term.TypeLevel[A]) extends Term.TypeLevel[T[A]]
      case class App2[T[_,_], A, B](tfun: Term.TypeLevel[T[__,__]], ta: Term.TypeLevel[A], tb: Term.TypeLevel[B]) extends Term.TypeLevel[T[A, B]]
      sealed trait Lam1[F[_]] extends Term.TypeLevel[F[__]]
      sealed trait Lam2[F[_,_]] extends Term.TypeLevel[F[__,__]]
      sealed abstract class Var[T] extends Term.TypeLevel[T]
      object Var:
        case object BooleanType extends Var[Boolean]
        case object IntType extends Var[Int]
        case object StringType extends Var[String]
        case object Function1Type extends Var[__ => __]
        case object ListType extends Var[List[__]]
        case class UserDefinedType[T](nme: String, impl: Option[Term.TypeLevel[T]]) extends Var[T]
        
    sealed trait ValueLevel[T] extends Term
    object ValueLevel:
      case class App1[A, B](fun: Term.ValueLevel[A => B], arg: Term.ValueLevel[A]) extends Term.ValueLevel[B]
      case class AppCtor1[T, A](tpe: Term.TypeLevel[T], arg: Term.ValueLevel[A]) extends Term.ValueLevel[T]
      case class AppVargs[A, B](fun: Term.ValueLevel[List[A] => B], vargs: ValueLevel[A]*) extends Term.ValueLevel[B]
      case class Lam1[A, B](a: Term.ValueLevel[A], b: Term.ValueLevel[B]) extends Term.ValueLevel[A => B]
      sealed abstract class Var[T] extends Term.ValueLevel[T]
      object Var:
        case class BooleanLiteral(b: Boolean) extends Var[Boolean]
        case class IntLiteral(i: Int) extends Var[Int]
        case class StringLiteral(s: String) extends Var[String]
        case class ListCtor[A]() extends Var[List[A] => List[A]]
        case class UserDefinedValue[T](nme: String, tpe: Term.TypeLevel[T], impl: Option[ValueLevel[T]]) extends Var[T]


        


