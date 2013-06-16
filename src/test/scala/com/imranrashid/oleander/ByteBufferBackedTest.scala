package com.imranrashid.oleander

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import java.nio.{FloatBuffer, ByteBuffer}
import ichi.bench.Thyme

class ByteBufferBackedTest extends FunSuite with ShouldMatchers with ProfileUtils {
  import ByteBufferBackedTest._

  test("simple arrays"){
    //make a big byte buffer, and chop it into some chunks
    val bb = ByteBuffer.allocate(400)
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
    val buf = new FloatArraySlice(ByteBuffer.allocate(n*4))
    val arrBuf = new FloatArrayAsBuffer(n)
    (0 until n).foreach { idx =>
      raw(idx) = idx * 2.4f
      wrapped(idx) = idx * 2.4f
      buf(idx) = idx * 2.4f
      arrBuf(idx) = idx * 2.4f
    }
    (raw, wrapped, buf, arrBuf)
  }

  def sumArrayish(arr: FloatArray): Float = {
    var idx = 0
    var sum = 0f
    while(idx < arr.length) {
      sum += arr(idx)
      idx += 1
    }
    sum
  }

  def sequentialProfile(th: Thyme) {

    println("******* Beginning Sequential Access Test *******")
    val n = 1e7.toInt
    val (raw, wrapped, buf, arrBuf) = initArrays(n)

    val rawWarmed = th.Warm{
      var idx = 0
      var sum = 0f
      while(idx < n) {
        sum += raw(idx)
        idx += 1
      }
      sum
    }

    val wrappedWarm = th.Warm{
      var idx = 0
      var sum = 0f
      while(idx < n) {
        sum += wrapped(idx)
        idx += 1
      }
      sum
    }
    val bufWarmed = th.Warm{
      var idx = 0
      var sum = 0f
      while(idx < n) {
        sum += buf(idx)
        idx += 1
      }
      sum
    }
    val arrBufWarmed = th.Warm{
      var idx = 0
      var sum = 0f
      while(idx < n) {
        sum += arrBuf(idx)
        idx += 1
      }
      sum
    }

    val exp = raw.sum
    check(th, rawWarmed, title="raw arrays")
    check(th, wrappedWarm, title="wrapped arrays")
    check(th, th.Warm(sumArrayish(wrapped)), title="wrapped arrays via Arrayish")
    check(th, bufWarmed, title="byte array in float buffer")
    check(th, th.Warm(sumArrayish(buf)), title="byte array in float buffer via Arrayish")
    th.pbenchOffWarm(title="buf direct vs. via Arrayish")(bufWarmed, wtitle="buf direct")(th.Warm(sumArrayish(buf)), vtitle = "sumArrayish")
    check(th, arrBufWarmed, title="float array in buffer")
    check(th, th.Warm(sumArrayish(arrBuf)), title="float array in buffer via Arrayish")


    var timeSum = 0l
    val nItrs = 20
    (0 until nItrs).foreach{ _ =>
      val start = System.nanoTime()
      sumArrayish(buf)
      timeSum += (System.nanoTime() - start)
    }
    println("hand time of byte array in float buffer via arrayish = " + (timeSum / (nItrs * 1e6)) + " ms")
    timeSum = 0l
    (0 until nItrs).foreach{ _ =>
      val start = System.nanoTime()
      bufWarmed.apply()
      timeSum += (System.nanoTime() - start)
    }
    println("hand time of byte array in float buffer via while  = " + (timeSum / (nItrs * 1e6)) + " ms")
    println("pclockN")
    th.pclockN(sumArrayish(buf))(m = 1, n = 20, op = th.uncertainPicker)


    println("ptimeN")
    th.ptimeN(sumArrayish(buf))(m = 1, n = 20, op = th.uncertainPicker, title="buf arrayish")
    th.ptimeN(bufWarmed.apply())(m = 1, n = 20, op = th.uncertainPicker, title="buf while")

    println("timeMany")

    {
      val t, el = th.createJS
      th.timeMany(bufWarmed.apply())(n = 20, op = th.uncertainPicker)(t, el)
      println(th.report(el, "", 20))
    }

  }

  def check(th: Thyme, warmed: Any, title: String) {
    //not sure of a better way to get the right type, the cast sucks
    println("result = " + th.pbenchWarm(warmed.asInstanceOf[th.Warm[Float]], title=title)) //should be (exp plusOrMinus eps)
  }

  def main(args: Array[String]) {
    sequentialProfile(ProfileUtils.thyme)
  }

  /*
  javap -classpath target/scala-2.10.2/test-classes -c com.imranrashid.oleander.ByteBufferBackedTest$ | more
  ~/scala/scala-2.10.2/bin/scala -classpath target/scala-2.10.2/classes/:target/scala-2.10.2/test-classes/:unmanaged/Thyme.jar:lib_managed/jars/org.scalatest/scalatest_2.10/scalatest_2.10-1.9.1.jar com.imranrashid.oleander.ByteBufferBackedTest
  or perhaps
  ~/scala/scala-2.10.2/bin/scala -J-XX:+UnlockDiagnosticVMOptions -J-XX:+PrintInlining -classpath target/scala-2.10.2/classes/:target/scala-2.10.2/test-classes/:unmanaged/Thyme.jar:lib_managed/jars/org.scalatest/scalatest_2.10/scalatest_2.10-1.9.1.jar com.imranrashid.oleander.ByteBufferBackedTest
   */
}


class SimpleWrappedFloatArray(val length: Int) extends Arrayish[Float] with FloatArray {
  val arr = new Array[Float](length)
  def apply(idx: Int): Float = arr(idx)
  def update(idx: Int, v: Float) {arr(idx) = v}
}

class FloatArrayAsBuffer(val length: Int) extends Arrayish[Float] with FloatArray {
  val arr = new Array[Float](length)
  val buf = FloatBuffer.wrap(arr)
  def apply(idx: Int): Float = buf.get(idx)
  def update(idx: Int, v: Float) {buf.put(idx, v)}

}