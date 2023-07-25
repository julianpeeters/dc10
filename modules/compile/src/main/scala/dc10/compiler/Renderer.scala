package dc10.compiler

trait Renderer[V, E, A]:
  def render(input: List[A]): String
  def renderErrors(errors: List[E]): String
  def version: V