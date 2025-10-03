package dc10

import cats.Id
import cats.data.NonEmptyList

trait Format[F[_]]:
  def output[A](log: List[A]): Either[List[Error], F[A]]

given Format[Id] =
  new Format[Id]:
    def output[A](log: List[A]): Either[List[Error], Id[A]] =
      log match
        case head :: next =>
          if next == Nil
          then Right(Id(head))
          else Left(List(CompilerError(s"expected a single entry but found ${log}")))
        case Nil =>
          Left(List(CompilerError("no AST to compile")))
      
given Format[NonEmptyList] =
  new Format[NonEmptyList]:
    def output[A](log: List[A]): Either[List[Error], NonEmptyList[A]] =
      NonEmptyList.fromList(log).fold(Left(List(CompilerError("no AST to compile")))): l =>
        Right(l)