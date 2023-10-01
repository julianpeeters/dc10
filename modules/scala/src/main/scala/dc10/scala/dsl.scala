package dc10.scala

import dc10.scala.predef.{Applications, Functions, Variables}
import dc10.scala.predef.datatype.{ComplexTypes, PrimitiveTypes}
import dc10.scala.predef.file.Files
import dc10.scala.predef.namespace.{Objects, Packages}

trait dsl

object dsl extends dsl
  with Applications.Mixins with Functions.Mixins with Variables.Mixins
  with ComplexTypes.Mixins with PrimitiveTypes.Mixins
  with Files.Mixins
  with Objects.Mixins with Packages.Mixins

