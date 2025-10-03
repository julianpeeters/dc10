package dc10

trait Renderer[F[_], V, A]:
  def render(input: F[A]): String
  def renderErrors(errors: List[Error]): String
  def version: V