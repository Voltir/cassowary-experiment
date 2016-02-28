package example

import example.c.{Equation, Expression}

import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.Dynamic.{literal => lit}
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSName

object c {

  @js.native
  @JSName("c.SimplexSolver")
  class SimplexSolver extends js.Object {
    def addConstraint(eq: Constraint): SimplexSolver = js.native
    def addStay(v: Variable): SimplexSolver = js.native
    def addEditVar(v: Variable): SimplexSolver = js.native
    def resolve(): Unit = js.native
    def beginEdit(): EditableSimplexSolver = js.native
  }

  @js.native
  @JSName("c.SimplexSolver")
  class EditableSimplexSolver extends js.Object {
    def suggestValue(v: Variable, value: Double): EditableSimplexSolver = js.native
    def resolve(): Unit = js.native
  }

  //Variables
  @js.native
  trait VariableArgs extends js.Object {
    val value: String
  }

  @js.native
  @JSName("c.Variable")
  class Variable(args: VariableArgs) extends js.Object {
    def value: Double = js.native
  }

  //Expression
  @js.native
  @JSName("c.Expression")
  class Expression(v: Variable) extends js.Object {
    def plus(v: Double | Variable): Expression = js.native
    def minus(v: Double | Variable): Expression = js.native
    def divide(v: Double | Variable): Expression = js.native
    def times(v: Double | Variable): Expression = js.native
  }

  //Constraints
  @js.native
  sealed trait Constraint extends js.Any

  @js.native
  @JSName("c.Inequality")
  class Inequality(v: Double | Variable, op: Int, target: Double | Expression) extends Constraint {

  }

  @js.native
  @JSName("c.Equation")
  class Equation(a: Double | Variable, b: Variable | Expression) extends Constraint {

  }

  @js.native
  @JSName("c")
  object ops extends js.Object {
    val GEQ: Int = js.native
    val LEQ: Int = js.native
    def approx(a: Double, b: Double): Boolean = js.native

  }
  //@js.native
  //@JSName("c.approx")
  //def approx(a: Double, b: Double): Boolean = js.native
}
