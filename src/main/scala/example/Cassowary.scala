package example

import example.c.{SimplexSolver}
import rx._
import c.implicits._

object Cassowary {

  sealed trait Constraint {
    private[Cassowary] def wrapped: c.Constraint
    private[Cassowary] val unwrapped: Rx[_]
    private[Cassowary] def wrap(solver: Solver)(implicit ctx: Ctx.Owner)
    private[Cassowary] def solved(): Unit
  }

  class EqualConstraint(left: VarConstraint, right: VarConstraint) extends Constraint {
    override val wrapped: c.Constraint = new c.Equation(left.wrapped, right.wrapped)
    override val unwrapped: Var[Unit] = Var()

    override def solved(): Unit = {
      left.solved()
      right.solved()
      unwrapped.recalc()
    }

    override private[Cassowary] def wrap(solver: Solver)(implicit ctx: Ctx.Owner) = {
      left.wrap(solver)
      right.wrap(solver)
      solver.simplex.addConstraint(wrapped)

      val watchLeft = left.unwrapped.triggerLater {
        right.solved()
        unwrapped.recalc()
      }

      val watchRight = right.unwrapped.triggerLater {
        left.solved()
        unwrapped.recalc()
      }
      solver.watching.append(watchLeft)
      solver.watching.append(watchRight)
    }
  }

  class BlahEqualConstraint(left: VarConstraint, right: Expr) extends Constraint {
    override val wrapped: c.Constraint = new c.Equation(left.wrapped, right.wrapped)
    override val unwrapped: Var[Unit] = Var()

    override def solved(): Unit = {
      left.solved()
      unwrapped.recalc()
    }

    override private[Cassowary] def wrap(solver: Solver)(implicit ctx: Ctx.Owner) = {
      left.wrap(solver)
      right.wrapExpr(solver)
      solver.simplex.addConstraint(wrapped)

      val watchLeft = left.unwrapped.triggerLater {
        unwrapped.recalc()
      }

      val watchRight = right.unwrapped.triggerLater {
        left.solved()
        unwrapped.recalc()
      }

      solver.watching.append(watchLeft)
      solver.watching.append(watchRight)
    }
  }

  class Equation(left: VarConstraint, right: Expr) extends Constraint {

    private[Cassowary] val unwrapped: Rx[_] = Var()

    override private[Cassowary] val wrapped: c.Equation = new c.Equation(left.wrapped,right.wrapped)

    private[Cassowary] def wrap(solver: Solver)(implicit ctx: Ctx.Owner): Unit = {
      println("EXPR WRAP CALLED: " + wrapped)
      solver.simplex.addConstraint(wrapped)
      println("ok..")
    }

    override private[Cassowary] def solved(): Unit = ()
  }

  trait Expr {
    private[Cassowary] def wrapped: c.Expression
    private[Cassowary] def unwrapped: Rx[_]

    private[Cassowary] def wrapExpr(solver: Solver)(implicit ctx: Ctx.Owner)


    //def /(other: Expr): Expr = new DivideExpr(wrapped,other)

    //def /(other: Double): Expr = new DivideConst(wrapped,other)

  }

//  class DivideExpr(left: c.Expression, right: Expr) extends Expr {
//    override def wrapped: c.Expression =
//      left.divide(right.wrapped)
//
//
//  }

//  class DivideConst(left: c.Expression, const: Double) extends Expr {
//    override def wrapped: c.Expression =
//      left.divide(const)
//  }

  class DivideBlah(blah: VarConstraint, other: Double) extends Expr {
    override val wrapped = new c.Expression(blah.wrapped).divide(other)
    override val unwrapped: Rx[_] = blah.unwrapped

    private[Cassowary] def wrapExpr(solver: Solver)(implicit ctx: Ctx.Owner) = {
      blah.wrap(solver)
    }
  }

  implicit class VarConstraint(override val unwrapped: Var[Double]) extends Constraint {

    private[Cassowary] val wrapped = new c.Variable(unwrapped.now.asArg)

//    private val dependencies: scala.collection.mutable.Buffer[DivideBlah] =
//      scala.collection.mutable.Buffer.empty

    override private[Cassowary] def wrap(solver: Solver)(implicit ctx: Ctx.Owner) = {
      solver.simplex.addEditVar(wrapped)
      val watcher = unwrapped.triggerLater {
        solver.simplex
          .beginEdit()
          .suggestValue(wrapped,unwrapped.now)
          .resolve()
        solved()
      }
      solver.watching.append(watcher)
    }

    def /(other: Double): Expr = {
      val wat = new DivideBlah(this, other)
      //dependencies.append(wat)
      wat
    }

    def ===(other: VarConstraint): Constraint = new EqualConstraint(this,other)

    def ===(other: Expr): Constraint = new BlahEqualConstraint(this,other)

    override def solved(): Unit = {
      unwrapped() = wrapped.value
    }
  }

  class Solver(implicit ctx: Ctx.Owner) {
    private[Cassowary] val simplex = new SimplexSolver()

    private[Cassowary] val watching: collection.mutable.Buffer[Obs] =
      collection.mutable.Buffer.empty[Obs]

    def addConstraint(constraint: Constraint): Solver = {
      constraint.wrap(this)
      this
    }
  }
}
