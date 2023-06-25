package dc10.schema

import cats.implicits.*
import dc10.compile.Compiler
import dc10.schema.definition.Statement
import java.nio.file.Path
import cats.data.Validated
import cats.Show

sealed trait Binding:
  type Tpe

object Binding:

  // File /////////////////////////////////////////////////////////////////////
  sealed abstract class File extends Binding:
    type Tpe = Nothing
    def path: Path
    def contents: List[Statement[Binding]]

  object File:
    def apply(p: Path, c: List[Statement[Binding]]): File =
      new File:
        def path: Path = p
        def contents: List[Statement[Binding]] = c

  // Templates ////////////////////////////////////////////////////////////////
  sealed abstract class CaseClass[T] extends Binding:
    type Tpe = T
    def nme: String
    def fields: List[Statement.ValDef]
    def body: List[Statement[Binding]]

  object CaseClass:

    def apply[T](
      n: T,
      fs: List[Statement.ValDef],
      bdy: List[Statement[Binding]]
    )(using sh: Show[T]): CaseClass[T] =
      new CaseClass:
        def nme = sh.show(n)
        def fields = fs
        def body = bdy

    def apply[T](
      n: T,
      ss: List[Statement[Binding]],
    )(using sh: Show[T]): Compiler.ErrorF[CaseClass[T]] =
      ss.traverse(s => s match
        case d: Statement.CaseClassDef => Left(???)
        case d: Statement.PackageDef   => Left(???)
        case d: Statement.ValDef       => Right(d)
        case d: Statement.UnsafeExpr   => Left(???)
      ).map(fs => CaseClass(n, fs, Nil))

  // Package //////////////////////////////////////////////////////////////////
  sealed abstract class Package extends Binding:
    type T = Nothing
  
  object Package:

    extension (pkg: Package)
      def getPath: Path =
        pkg match
          case Basic(nme, nst) => Path.of(nme).resolve(nst.pkg.getPath)
          case Empty(ms) => Path.of("")

      def addMember(stmt: Statement[Binding]): Package =
        pkg match
          case Basic(nme, nst) => nst.pkg.addMember(stmt)
          case Empty(ms) => Empty(ms :+ stmt)

    case class Basic(
      nme: String,
      nst: Statement.PackageDef
    ) extends Package

    case class Empty(
      ms : List[Statement[Binding]]
    ) extends Package


  // Type Level ///////////////////////////////////////////////////////////////
  sealed abstract class Type[T] extends Binding:
    type Tpe = T
    def nme: String
    def impl: Option[Type[T]]

  object Type:
    
    def apply[T](n: String, i: Option[Type[T]]): Type[T] =
      new Type:
        def nme = n
        def impl = i

    def boolean: Type[Boolean] =
      new Type:
        def nme: String = "Boolean"
        def impl = None

    def int: Type[Int] =
      new Type:
        def nme: String = "Int"
        def impl = None

    def string: Type[String] =
      new Type:
        def nme: String = "String"
        def impl = None

  // Value Level //////////////////////////////////////////////////////////////
  sealed abstract class Value[T] extends Binding:
    type Tpe = T
    def nme: String
    def tpe: Type[T]
    def impl: Option[Value[T]]

  object Value:

    def apply[T](
      n: String,
      t: Type[T],
      i: Option[Value[T]]
    ): Value[T] =
      new Value:
        def nme: String = s"${n}"
        def tpe: Type[T] = t
        def impl: Option[Value[T]] = i
    
    def apply[T](
      nme: String,
      tpe: Compiler.ErrorF[Type[T]],
    ): Compiler.ErrorF[Value[T]] =
      (
        Validated.valid(nme),
        Validated.fromEither(tpe)
      ).mapN(
        (n, t) =>
          Value(n, t, None)
      ).toEither

    def apply[T](
      nme: String,
      tpe: Compiler.ErrorF[Type[T]],
      impl: Compiler.ErrorF[Value[T]]
    ): Compiler.ErrorF[Value[T]] =
      (
        Validated.valid(nme),
        Validated.fromEither(tpe),
        Validated.fromEither(impl)
      ).mapN(
        (n, t, i) =>
          Value(n, t, Some(i))
      ).toEither

    def boolean(b: Boolean): Value[Boolean] =
      Value(s"${b.toString}", Type.boolean, None)
      
    def int(i: Int): Value[Int] =
      Value(s"${i.toString}", Type.int, None)
      
    def string(s: String): Value[String] =
      Value(s"\"${s}\"", Type.string, None)