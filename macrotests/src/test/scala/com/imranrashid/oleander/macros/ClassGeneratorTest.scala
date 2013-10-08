package com.imranrashid.oleander.macros

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/**
 *
 */
class ClassGeneratorTest extends FunSuite with ShouldMatchers {


  test("printlnMacros"){
    ClassGenerator.printMacro("wakka wakka")
  }

  test("classDefMacros"){
    ClassGenerator.classExpandMacro(this)

  }
}


class BasicClass {
  var z = 0
}