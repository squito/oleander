package com.imranrashid.oleander.macros

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/**
 *
 */
class ClassGeneratorTest extends FunSuite with ShouldMatchers {


  test("macros"){
    println("blah")
    ClassGenerator.macroFoo("wakka wakka")
  }
}


trait BasicTrait {
  def x: Int
  def y: Float
}