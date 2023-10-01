package dc10.scala.predef.namespace

import cats.data.StateT
import dc10.scala.ast.Statement.ObjectDef
import dc10.scala.ast.{Statement, Symbol}
import dc10.scala.ctx.ext
import dc10.scala.error.ErrorF
import dc10.scala.ast.Statement.*

trait Objects[F[_]]:
  def OBJECT[T, A](name: String, contents: F[A]): F[List[Statement]]

object Objects:

  trait Mixins extends Objects[[A] =>> StateT[ErrorF, List[Statement], A]]:
    
    def OBJECT[T, A](name: String, contents: StateT[ErrorF, List[Statement], A]): StateT[ErrorF, List[Statement], List[Statement]] =
      for
        c <- StateT.liftF[ErrorF, List[Statement], List[Statement]](contents.runEmptyS)
        o <- StateT.pure[ErrorF, List[Statement], Symbol.Object[T]](Symbol.Object(None, name, c.map(s => s match
          case d@CaseClassDef(i, sp) => CaseClassDef(d.caseclass, i + 1)(using sp)
          case d@ObjectDef(i, sp) => ObjectDef(d.obj, i + 1)(using sp)
          case d@PackageDef(i, sp) => PackageDef(d.pkg, i + 1)(using sp)
          case TypeExpr(tpe) => ???
          case ValueExpr(value) => ???
          case d@ValDef(i, sp) => ValDef(d.value)(i + 1)(using sp)
        )))
        d <- StateT.pure[ErrorF, List[Statement], ObjectDef](ObjectDef(o, 0))
        _ <- StateT.modifyF[ErrorF, List[Statement]](ctx => ctx.ext(d))
      yield c
