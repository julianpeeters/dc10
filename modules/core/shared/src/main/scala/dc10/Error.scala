package dc10

sealed trait Error
case class CompilerError(e: String) extends Error
case class LanguageError[E](e: E) extends Error