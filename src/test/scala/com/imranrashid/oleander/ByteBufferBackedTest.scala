package com.imranrashid.oleander

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import java.nio.{FloatBuffer, ByteBuffer, ByteOrder}
import ichi.bench.Thyme

class ByteBufferBackedTest extends FunSuite with ShouldMatchers with ProfileUtils {
  import ByteBufferBackedTest._

  test("simple arrays"){
    //make a big byte buffer, and chop it into some chunks
    val bb = ByteBuffer.allocateDirect(400).order(ByteOrder.nativeOrder)
    val bb1 = bb.slice()
    bb1.limit(40)
    val arr1 = new FloatArraySlice(bb1)
    (0 until 10).foreach{idx =>arr1(idx) = idx * 1.5f}
    arr1.length should be (10)

    bb.position(60)
    val bb2 = bb.slice()
    bb2.limit(30 * 4)
    val arr2 = new FloatArraySlice(bb2)
    (0 until 30).foreach{idx => arr2(idx) = idx * 2.9f}
    arr2.length should be (30)
  }

  testProfile("profile sequential"){sequentialProfile}

//  testProfile("profile random") { th =>
//    println("******* Beginning Random Access Test *******")
//    val n = 1e7.toInt
//    val (raw, wrapped, buf, arrBuf) = initArrays(n)
//
//    import collection.JavaConverters._
//    val order = new util.ArrayList[Int](n)
//    (0 until n).foreach{i => order.add(i)}
//    Collections.shuffle(order)
//    val o2 = order.asScala
//
//    val rawWarmed = th.Warm{
//      var idx = 0
//      var sum = 0f
//      while(idx < n) {
//        sum += raw(o2(idx))
//        idx += 1
//      }
//      sum
//    }
//    val wrappedWarm = th.Warm{
//      var idx = 0
//      var sum = 0f
//      while(idx < n) {
//        sum += wrapped(o2(idx))
//        idx += 1
//      }
//      sum
//    }
//    val bufWarmed = th.Warm{
//      var idx = 0
//      var sum = 0f
//      while(idx < n) {
//        sum += buf(o2(idx))
//        idx += 1
//      }
//      sum
//    }
//    val arrBufWarmed = th.Warm{
//      var idx = 0
//      var sum = 0f
//      while(idx < n) {
//        sum += arrBuf(o2(idx))
//        idx += 1
//      }
//      sum
//    }
//
//
//    th.pbenchWarm(rawWarmed, title="raw arrays")
//    th.pbenchWarm(wrappedWarm, title="wrapped arrays")
//    th.pbenchWarm(bufWarmed, title="byte array in float buffer")
//    th.pbenchWarm(arrBufWarmed, title="float array in float buffer")
//  }

}

object ByteBufferBackedTest {
  def initArrays(n: Int) = {
    val raw = new Array[Float](n)
    val wrapped = new SimpleWrappedFloatArray(n)
    val buf = new FloatArraySlice(ByteBuffer.allocateDirect(n*4).order(ByteOrder.nativeOrder))

    val arrBuf = new FloatArrayAsBuffer(n)
    (0 until n).foreach { idx =>
      raw(idx) = idx * 2.4f
      wrapped(idx) = idx * 2.4f
      buf(idx) = idx * 2.4f
      arrBuf(idx) = idx * 2.4f
    }
    (raw, wrapped, buf, arrBuf)
  }

  def sequentialProfile(th: Thyme) {
    val n = 1e7.toInt
    val (rawArray, _, buf, _) = initArrays(n)
    //to get accurate timing here, you CANNOT wrap the implementations up in a common trait, b/c then you
    // add the cost of dynamic dispatch, which dwarfs the floating point operations
    println("result = " + th.pbench({
      var idx = 0
      var sum = 0f
      while(idx < n) {
        sum += rawArray(idx)
        idx += 1
      }
      sum
    }, title="raw arrays"))
    val rawBB = buf.bb
    println("result = " + th.pbench({
      var idx = 0
      var sum = 0f
      while(idx < n) {
        sum += rawBB.getFloat(idx * 4)
        idx += 1
      }
      sum
    }, title="byte buffer"))
    val rawFloatBuf = buf.floatBuffer
    println("result = " + th.pbench({
      var idx = 0
      var sum = 0f
      while(idx < n) {
        sum += rawFloatBuf.get(idx)
        idx += 1
      }
      sum
    }, title="float buffer"))
  }
  def main(args: Array[String]) {
    val th = ProfileUtils.thyme
    sequentialProfile(th)
  }

  /*
  javap -classpath target/scala-2.10.2/test-classes -c com.imranrashid.oleander.ByteBufferBackedTest | more
  ~/scala/scala-2.10.2/bin/scala -classpath target/scala-2.10.2/classes/:target/scala-2.10.2/test-classes/:unmanaged/Thyme.jar:lib_managed/jars/org.scalatest/scalatest_2.10/scalatest_2.10-1.9.1.jar com.imranrashid.oleander.ByteBufferBackedTest
  or
  ~/scala/scala-2.10.2/bin/scala -J-XX:+PrintCompilation -classpath target/scala-2.10.2/classes/:target/scala-2.10.2/test-classes/:unmanaged/Thyme.jar:lib_managed/jars/org.scalatest/scalatest_2.10/scalatest_2.10-1.9.1.jar com.imranrashid.oleander.ByteBufferBackedTest
  or perhaps
  ~/scala/scala-2.10.2/bin/scala -J-XX:+UnlockDiagnosticVMOptions -J-XX:+PrintInlining -classpath target/scala-2.10.2/classes/:target/scala-2.10.2/test-classes/:unmanaged/Thyme.jar:lib_managed/jars/org.scalatest/scalatest_2.10/scalatest_2.10-1.9.1.jar com.imranrashid.oleander.ByteBufferBackedTest
   */
}


class SimpleWrappedFloatArray(val length: Int) extends ArrayLike[Float] {
  val arr = new Array[Float](length)
  def apply(idx: Int): Float = arr(idx)
  def update(idx: Int, v: Float) {arr(idx) = v}
  def sumInImpl: Float = {
    var idx = 0
    var sum = 0f
    while(idx < length) {
      sum += this(idx)
      idx += 1
    }
    sum
  }
}

class FloatArrayAsBuffer(val length: Int) extends ArrayLike[Float] {
  val arr = new Array[Float](length)
  val buf = FloatBuffer.wrap(arr)
  def apply(idx: Int): Float = buf.get(idx)
  def update(idx: Int, v: Float) {buf.put(idx, v)}
  def sumInImpl: Float = {
    var idx = 0
    var sum = 0f
    while(idx < length) {
      sum += this(idx)
      idx += 1
    }
    sum
  }

}
