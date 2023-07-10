package dc10.dsl

import dc10.dsl.predef.{Applications, Functions, Variables}
import dc10.dsl.predef.datatype.{ComplexTypes, PrimitiveTypes}

trait Scala
  
object Scala extends Scala
  with ComplexTypes.Mixins with PrimitiveTypes.Mixins
  with Applications.Mixins with Functions.Mixins with Variables.Mixins
