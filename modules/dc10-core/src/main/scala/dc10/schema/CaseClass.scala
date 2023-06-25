package dc10.schema

import cats.implicits.*
import dc10.compile.Compiler
import dc10.schema.definition.Statement

sealed abstract class CaseClass:
  def nme: String
  def fields: List[Statement.ValDef]
  def body: List[Statement]

object CaseClass:

  def apply(
    n: String,
    fs: List[Statement.ValDef],
    bdy: List[Statement]
  ): CaseClass =
    new CaseClass:
      def nme = n
      def fields = fs
      def body = bdy

  def apply(
    n: String,
    ss: List[Statement],
  ): Compiler.ErrorF[CaseClass] =
    ss.traverse(s => s match
      case Statement.CaseClassDef(c, i) => Left(???)
      case Statement.PackageDef(pkg)    => Left(???)
      case d@Statement.ValDef(v, i)     => Right(d)
      case Statement.UnsafeExpr(value)  => Left(???)
    ).map(fs => CaseClass(n, fs, Nil))