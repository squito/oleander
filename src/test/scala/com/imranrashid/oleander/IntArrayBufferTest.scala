package com.imranrashid.oleander

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import java.nio.{ByteBuffer, IntBuffer}
import ichi.bench.Thyme
import java.io.File

/**
 *
 */
class IntArrayBufferTest extends FunSuite with ShouldMatchers {

}

object IntArrayBufferTest {
  def initArrays(n: Int) = {
    val raw = new Array[Int](n)
    val wrapped = new IntArrayAsBuffer(raw)
    val buf = new IntArrayBuffer(ByteBuffer.allocate(n*4))
    (0 until n).foreach { idx =>
      raw(idx) = idx
      wrapped(idx) = idx
      buf(idx) = idx
    }
    (raw, wrapped, buf)
  }

  def sequentialProfile(th: Thyme) {
    val n = 1e7.toInt
    val tsv = new TsvPrinter(th, new File("int_buffer_profile.tsv"))
    val (rawArray, arrBuf, buf) = initArrays(n)
    //to get accurate timing here, you CANNOT wrap the implementations up in a common trait, b/c then you
    // add the cost of dynamic dispatch, which dwarfs the floating point operations
    println("result = " + tsv.tsvBench({
      var idx = 0
      var sum = 0
      while(idx < n) {
        sum += rawArray(idx)
        idx += 1
      }
      sum
    }, title="int[]"))
    val rawBB = buf.bb
    println("result = " + tsv.tsvBench({
      var idx = 0
      var sum = 0
      while(idx < n) {
        sum += rawBB.getInt(idx * 4)
        idx += 1
      }
      sum
    }, title="ByteBuffer.getInt"))
    println("result = " + tsv.tsvBench({
      var idx = 0
      var sum = 0
      while(idx < n) {
        sum += buf(idx)
        idx += 1
      }
      sum
    }, title="byte[] in IntBuffer"))
    println("result = " + tsv.tsvBench({
      var idx = 0
      var sum = 0
      while(idx < n) {
        sum += arrBuf(idx)
        idx += 1
      }
      sum
    }, title="int[] in IntBuffer"))

    tsv.close()
  }

  def main(args: Array[String]) {
    val th = ProfileUtils.thyme
    sequentialProfile(th)
  }


}

class IntArrayAsBuffer(val arr: Array[Int]) extends ArrayLike[Int] {
  val buf = IntBuffer.wrap(arr)
  def apply(idx: Int): Int = buf.get(idx)
  def update(idx: Int, v: Int) {buf.put(idx, v)}
  def length = arr.length
}