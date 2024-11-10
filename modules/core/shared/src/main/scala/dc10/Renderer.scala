package dc10

trait Renderer[C, E, V]:
  def render(input: List[C]): String
  def renderErrors(errors: List[E]): String
  def version: V