package dc10.scala.error

import dc10.scala.ast.{Statement, Symbol}

type ErrorF[A] = Either[List[CompileError], A]

sealed trait CompileError
case class IdentifierStatementExpected(butFound: Statement) extends CompileError
case class IdentifierSymbolExpected(butFound: Symbol) extends CompileError
