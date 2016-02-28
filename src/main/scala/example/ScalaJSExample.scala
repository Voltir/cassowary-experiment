package example

import example.c.Variable
import rx._
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import scalatags.JsDom.all._
import framework.Framework._



@JSExport
object ScalaJSExample {

  implicit class WurtInt(val num: Int) extends AnyVal {
    def asArg: c.VariableArgs =
      scalajs.js.Dynamic.literal(value=num).asInstanceOf[c.VariableArgs]
  }

  implicit class WurtDouble(val num: Double) extends AnyVal {
    def asArg: c.VariableArgs =
      scalajs.js.Dynamic.literal(value=num).asInstanceOf[c.VariableArgs]
  }

  lazy val innerHeight = Var(dom.window.innerHeight)

  lazy val innerWidth = Var(dom.window.innerWidth)


  def throttle[T <: dom.Event](target: String)(f: T => Unit) = {
    var running = false
    val func = (evt: T) => {
      if(!running) {
        running = true
        dom.window.requestAnimationFrame((_:Double) => {
          f(evt)
          running = false
        })
      }
    }
    dom.window.addEventListener(target,func)
  }

  val transform = scalatags.generic.Style("transform","transform")

  val cx = Var(0.0)
  val cy = Var(0.0)

  def lolcenter()(implicit ctx: Ctx.Owner) = {
    transform := Rx { s"translate(${cx()}px, ${cy()}px)" }
  }

  def aBox()(implicit ctx: Ctx.Owner): HtmlTag = div(width:=50.px,height:=50.px,backgroundColor:="Red", lolcenter())

  @JSExport
  def main(content: dom.html.Div): Unit = {
    import Ctx.Owner.Unsafe._
    content.innerHTML = ""
    content.appendChild(aBox.render)

    println(dom.window.innerHeight)
    println(dom.window.innerWidth)

    throttle("resize") { (e: dom.Event) =>
      Var.set(
        innerHeight -> dom.window.innerHeight,
        innerWidth -> dom.window.innerWidth
      )
    }

    dom.console.log(dom.window)

//    val solver = new c.SimplexSolver()
//    val x = new c.Variable(167.asArg)
//    val y = new c.Variable(2.asArg)
//    println(c.ops.approx(x.value,y.value))
//
//    println(x.value)
//    println(y.value)
//
//    //println(c.approx(x.value,y.value))
//    val eq = new c.Equation(x, new c.Expression(y))
//    solver.addConstraint(eq)
//
//    println(x.value)
//    println(y.value)
//    println(c.ops.approx(x.value,y.value))

//    val s2 = new c.SimplexSolver()
//    val x2 = new c.Variable(10.asArg)
//    val width = new c.Variable(10.asArg)
//    val right = new c.Expression(x2).plus(width)
//
//    println(x2)
//    println(width)
//    println(right)
//    val ieq = new c.Inequality(100,c.ops.GEQ, right)
//    s2.addStay(width).addConstraint(ieq)
//    println(x2.value)
//    println(width)

    val solver = new c.SimplexSolver()

    val boxCx = new Variable(0.asArg)
    val boxCy = new Variable(0.asArg)
    val windowCx = new Variable(dom.window.innerWidth.asArg)
    val windowCy = new Variable(dom.window.innerHeight.asArg)

    println(windowCx,boxCx)
    println(windowCy,boxCy)

    solver
      .addEditVar(windowCx)
      .addEditVar(windowCy)
      .addConstraint(new c.Equation(boxCx,new c.Expression(windowCx).divide(2)))
      .addConstraint(new c.Equation(boxCy,new c.Expression(windowCy).divide(2)))

    println(windowCx,boxCx)
    println(windowCy,boxCy)

    Var.set(cx -> boxCx.value, cy -> boxCy.value)
    throttle("resize") { (e: dom.Event) =>
      solver
        .beginEdit()
        .suggestValue(windowCx,dom.window.innerWidth)
        .suggestValue(windowCy,dom.window.innerHeight)
        .resolve()
      Var.set(cx -> boxCx.value, cy -> boxCy.value)
    }
  }
}
