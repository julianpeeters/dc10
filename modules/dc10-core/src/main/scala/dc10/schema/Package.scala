package dc10.schema

import dc10.schema.definition.Statement
import java.nio.file.Path

sealed abstract class Package
object Package:

  extension  (pkg: Package)
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