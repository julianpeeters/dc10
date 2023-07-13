package dc10.scala

import dc10.scala.ctx.predef.{Applications, Functions, Variables}
import dc10.scala.ctx.predef.datatype.{ComplexTypes, PrimitiveTypes}

trait dsl

object dsl extends dsl
  with ComplexTypes.Mixins with PrimitiveTypes.Mixins
  with Applications.Mixins with Functions.Mixins with Variables.Mixins

