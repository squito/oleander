package com.imranrashid.oleander.macros


import language.experimental.macros
import reflect.macros.Context

object ClassGenerator {
  def macroFoo(s: String) = macro macroFooImpl

  def macroFooImpl(c: Context)(s: c.Expr[String]) : c.Expr[Any] = {
    import c.universe._
    println(showRaw(reify{"x " + 5}))
    val x = c.Expr(Apply(Select(Literal(Constant("x ")), newTermName("$plus")), List(s.tree)))
    reify{println(x.splice)}
  }
}
