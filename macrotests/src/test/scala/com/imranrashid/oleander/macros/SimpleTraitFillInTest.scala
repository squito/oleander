package com.imranrashid.oleander.macros

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite

class SimpleTraitFillInTest extends FunSuite with ShouldMatchers {

  test("fill in trait defs") {
    //the trait is already declared, but the macro actually supplies the defs
    @FillTraitDefs class Foo extends SimpleTrait {}
    val y = new Foo()
    y.x should be (5)
    y.y should be (7.0f)
  }

  test("add trait as super"){
    //we need to both add in the trait as a super, and also fill in the defs
    @AddTraitAsSuper class Blah {}
    val x = new Blah()
    x.isInstanceOf[SimpleTrait] should be (true)
    x.x should be (5)
    x.y should be (7.0f)
  }
}
