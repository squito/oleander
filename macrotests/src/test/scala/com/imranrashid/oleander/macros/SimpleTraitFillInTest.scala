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

  test("add trait with quasiquotes"){
    @QuasiQuoteAddTrait class Ooga {}
    val z = new Ooga()
    z.isInstanceOf[SimpleTrait] should be (true)
    z.x should be (5)
    z.y should be (7.0f)

    //make sure we keep original defs also

    @QuasiQuoteAddTrait class Wakka {
      def q = "hi there"
    }
    val w = new Wakka()
    w.x should be (5)
    w.y should be (7.0f)
    w.q should be ("hi there")

  }
}
