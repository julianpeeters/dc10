package dc10.ast.predef.datatype

// import cats.Applicative
// import dc10.ast.Binding.Term

sealed trait Complex[F[_]]
  // def function1Value[A, B](a: Term.ValueLevel[A], b: Term.ValueLevel[B]): F[Term.ValueLevel[A => B]]
  // def listType[A]: F[Term.TypeLevel.Var.ListType]
  // def listValue[A](la: List[A]): F[Term.ValueLevel[List[A]]]


object Complex

  // def apply[F[_]: Applicative]: Complex[F] =

      // def function1Value[A, B](a: Term.ValueLevel[A], b: Term.ValueLevel[B]): F[Term.ValueLevel[A => B]] =
      //   Applicative[F].pure(Term.ValueLevel.Lam.Function1Value(a, b))

      
      // def listType[A](targ: F[TypeLevel[A]]): F[TypeLevel[List[A]]] =
      //   Functor[F].map(targ)(t => t match
      //     case App(targ) => ???
      //     case Lam() => ???
      //     case TypeLevel.Lit.ListT(nme) => ???
      //     case Var(nme, impl) => TypeLevel.Lit.ListT(nme)
      //   )
        
          
          

      // def listValue[A](la: List[A]): F[Term.ValueLevel[List[A]]] = ???
        //  Functor[F].pure(ValueLevel.Lit[List[A]](ValueLevel.Name.Predefined.ListAV(b)))