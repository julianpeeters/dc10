package dc10.renderer

trait Renderer[V, E, A]:
  def render(input: A): String
  def renderError(error: E): String
  def version: V
