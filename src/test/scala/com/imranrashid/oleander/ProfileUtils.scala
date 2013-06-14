package com.imranrashid.oleander

import org.scalatest.FunSuite
import org.scalatest.Tag
import ichi.bench.Thyme


/**
 *
 */
trait ProfileUtils {
  self: FunSuite =>
  def testProfile(name:String, tags: Tag*)(testFun: Thyme => Unit) {
    test(name, tags: _*) {
      testFun(ProfileUtils.thyme)
    }
  }
}

object ProfileUtils {
  val warmed = false
  lazy val thyme = if (warmed) Thyme.warmed(verbose=print) else new Thyme()
}
