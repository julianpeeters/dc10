package dc10.scala

import dc10.scala.ctx.predef.{Applications, Functions, Variables}
import dc10.scala.ctx.predef.datatype.{ComplexTypes, PrimitiveTypes}
import dc10.scala.ctx.predef.file.Files
import dc10.scala.ctx.predef.namespace.Packages

trait dsl

object dsl extends dsl
  with Applications.Mixins with Functions.Mixins with Variables.Mixins
  with ComplexTypes.Mixins with PrimitiveTypes.Mixins
  with Files.Mixins
  with Packages.Mixins

