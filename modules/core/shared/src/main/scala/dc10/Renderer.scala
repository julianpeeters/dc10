package dc10

trait Renderer[V, E, A]:
  def render(input: List[A]): String
  def renderErrors(errors: List[E]): String
  def version: V