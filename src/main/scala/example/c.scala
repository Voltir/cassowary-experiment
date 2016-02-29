package example


import scala.scalajs.js
import scala.scalajs.js.`|`
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

  @js.native
  sealed trait Constraint extends js.Any

  //Expression
  @js.native
  @JSName("c.Expression")
  class Expression(v: Variable) extends js.Object {
    def plus(v: Double | Variable | Expression): Expression = js.native
    def minus(v: Double | Variable | Expression): Expression = js.native
    def divide(v: Double | Variable | Expression): Expression = js.native
    def times(v: Double | Variable | Expression): Expression = js.native
  }

  @js.native
  @JSName("c.Inequality")
  class Inequality(v: Double | Variable, op: Int, target: Double | Expression) extends Constraint {

  }

  @js.native
  @JSName("c.Equation")
  class Equation(a: Double | Variable, b: Double | Variable | Expression) extends Constraint {

  }

  //Variables
  @js.native
  trait VariableArgs extends Constraint {
    val value: String
  }

  @js.native
  @JSName("c.Variable")
  class Variable(args: VariableArgs) extends Constraint {
    def value: Double = js.native
  }

  @js.native
  @JSName("c")
  object ops extends js.Object {
    val GEQ: Int = js.native
    val LEQ: Int = js.native
    def approx(a: Double, b: Double): Boolean = js.native

  }

  object implicits {
    type ExprType = Double | Variable | Expression

    implicit class WurtInt(val num: Int) extends AnyVal {
      def asArg: c.VariableArgs =
        scalajs.js.Dynamic.literal(value=num).asInstanceOf[c.VariableArgs]
    }

    implicit class WurtDouble(val num: Double) extends AnyVal {
      def asArg: c.VariableArgs =
        scalajs.js.Dynamic.literal(value=num).asInstanceOf[c.VariableArgs]
    }
  }
}
