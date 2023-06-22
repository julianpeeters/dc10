package dc10.schema

import dc10.schema.define.{Statement, ValDef}

case class CaseClass(nme: String, fields: List[ValDef], body: List[Statement])