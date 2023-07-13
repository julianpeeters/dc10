package dc10

trait Compiler[F[_], A]:
  def generate[V](input: List[A]): F[List[VirtualFile]]

object Compiler:
  
  type ErrorF[A] = Either[List[Compiler.Error], A]
  sealed trait Error



























  // type Γ = List[Statement]

  // given compiler[V, A] (
  //   using
  //     C: Config[V],
  //     D: Renderer[V, SourceFile],
  // ): Compiler[ErrorF, SourceFile] =
  //   new Compiler[ErrorF, SourceFile]:
  //     def generate(
  //       res: List[SourceFile],
  //       cfg: Config[V]
  //     ): Compiler.ErrorF[List[VirtualFile]] =
  //       Right(
  //         res.map(fileDef =>
  //           VirtualFile(
  //             fileDef.file.path,
  //             fileDef.file.contents.map(D.render).mkString)))




  // extension (ctx: Γ)
  //   def ext(s: Statement): Compiler.ErrorF[Γ] =
  //     namecheck(s).map(ctx :+ _)
  //   def namecheck(s: Statement): Compiler.ErrorF[Statement] =
  //     // TODO
  //     Right(s)

  // extension [F[_]: FlatMap, L: Monoid, V, A] (ast: StateT[F, L, Unit])
  //   // def compile: F[L] = ast.runEmptyS
  //   def compile = new Compiler[F, A]:

  //     extension [F[_], L](res: F[L])
  //       override def toString[V](
  //         using
  //           F: Functor[F],
  //           D: Renderer[V, A]
  //         ): F[String] = ???

  //     extension [F[_], A](res: F[List[A]])
  //       override def toVirtualFile[V](
  //         using
  //           F: FlatMap[F],
  //           C: Compiler[F, A],
  //           D: Config[V]
  //         ): F[List[VirtualFile]] = ???

      // def toVirtualFile(res: List[A], cfg: Config[V]): F[List[VirtualFile]] = ???
      
  
  // extension [F[_]: Functor, A] (res: F[Γ])
  //   def toString[V](
  //     using
  //       D: Renderer[V, A]
  //   ): F[String] =
  //     Functor[F].map(res)(r => r.map(D.renderStatement).mkString("\n"))

  // extension [F[_]: FlatMap, A] (res: F[List[A]])
  //   def toVirtualFile[V](
  //     using
  //       C: Compiler[F, V, A],
  //       D: Config[V]
  //   ): F[List[VirtualFile]] =
  //     FlatMap[F].flatMap(res)(r => C.generate(r, D))
  