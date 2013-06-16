package com.imranrashid.oleander

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import java.nio.{FloatBuffer, ByteBuffer}
import ichi.bench.Thyme
import java.util
import java.util.Collections

class ByteBufferBackedTest extends FunSuite with ShouldMatchers with ProfileUtils {

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

  testProfile("profile sequential"){th =>
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

    th.pbenchWarm(rawWarmed, title="raw arrays")
    th.pbenchWarm(wrappedWarm, title="wrapped arrays")
    th.pbenchWarm(bufWarmed, title="byte array in float buffer")
    th.pbenchWarm(arrBufWarmed, title="float array in buffer")
  }

  testProfile("profile random") { th =>
    val n = 1e7.toInt
    val (raw, wrapped, buf, arrBuf) = initArrays(n)

    import collection.JavaConverters._
    val order = new util.ArrayList[Int](n)
    (0 until n).foreach{i => order.add(i)}
    Collections.shuffle(order)
    val o2 = order.asScala

    val rawWarmed = th.Warm{
      var idx = 0
      var sum = 0f
      while(idx < n) {
        sum += raw(o2(idx))
        idx += 1
      }
      sum
    }
    val wrappedWarm = th.Warm{
      var idx = 0
      var sum = 0f
      while(idx < n) {
        sum += wrapped(o2(idx))
        idx += 1
      }
      sum
    }
    val bufWarmed = th.Warm{
      var idx = 0
      var sum = 0f
      while(idx < n) {
        sum += buf(o2(idx))
        idx += 1
      }
      sum
    }
    val arrBufWarmed = th.Warm{
      var idx = 0
      var sum = 0f
      while(idx < n) {
        sum += arrBuf(o2(idx))
        idx += 1
      }
      sum
    }


    th.pbenchWarm(rawWarmed, title="raw arrays")
    th.pbenchWarm(wrappedWarm, title="wrapped arrays")
    th.pbenchWarm(bufWarmed, title="byte array in float buffer")
    th.pbenchWarm(arrBufWarmed, title="float array in float buffer")
  }

}


class SimpleWrappedFloatArray(val size: Int) {
  val arr = new Array[Float](size)
  def apply(idx: Int): Float = arr(idx)
  def update(idx: Int, v: Float) {arr(idx) = v}
}

class FloatArrayAsBuffer(val size: Int) {
  val arr = new Array[Float](size)
  val buf = FloatBuffer.wrap(arr)
  def apply(idx: Int): Float = buf.get(idx)
  def update(idx: Int, v: Float) {buf.put(idx, v)}

}