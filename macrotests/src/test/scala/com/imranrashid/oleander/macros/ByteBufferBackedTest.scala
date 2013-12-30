package com.imranrashid.oleander.macros

import org.scalatest.{Matchers, FunSuite}
import java.nio.ByteBuffer

class ByteBufferBackedTest extends FunSuite with Matchers {
  test("immutable") {
    @ByteBufferBacked[BBTest] class Blah(var bb: ByteBuffer, var position: Int = 0)
    val bb = ByteBuffer.allocate(80)
    val b = new Blah(bb)
    bb.putInt(7)
    bb.putFloat(5.4f)
    bb.putInt(3)

    b.x should be (7)
    b.y should be (5.4f)
    b.z should be (3)
    b.numBytes should be (12)

    b.setBuffer(bb, 30)
    b.x should be (0)
    b.y should be (0f)
    b.z should be (0)
  }

  test("mutable") {
    @MutableByteBufferBacked[BBTest] class Foo(var bb: ByteBuffer, var position: Int = 0)
    val bb = ByteBuffer.allocate(80)
    val b = new Foo(bb)
    b.x = 19
    b.y = -2.3f
    b.z = 32

    b.x should be (19)
    b.y should be (-2.3f)
    b.z should be (32)
    b.numBytes should be (12)

    b.setBuffer(bb, 40)
    b.x = 42
    b.y = 5.7f
    b.z = 12

    b.x should be (42)
    b.y should be (5.7f)
    b.z should be (12)

  }
}


trait BBTest {
  def x: Int
  def y: Float
  def z: Int
}

