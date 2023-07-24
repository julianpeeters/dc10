package dc10.renderer

trait Renderer[V, E, A]:
  // type Cont[_]
  // type Defn  //[_]
  // // type Ent
  // type Err
  
  def render(input: List[A]): String
  def renderErrors(errors: List[E]): String
  def version: V


// trait Renderer[V, E, F[_], G[_], A]:
//   def render(input: F[G[A]): String
//   def renderError(error: E): String
//   def version: V
