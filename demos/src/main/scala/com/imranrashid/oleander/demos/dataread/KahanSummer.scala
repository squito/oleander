package com.imranrashid.oleander.demos.dataread

import com.imranrashid.oleander.macros.MutableByteBufferBacked
import java.nio.ByteBuffer

/* Note that in practice, it makes more sense to just use doubles, but this is just an example
 */


trait KahanSummerData {
  def sum: Float
  def sum_=(f: Float)
  def c: Float
  def c_=(f: Float)
}

trait KahanSummer {
  self: KahanSummerData =>

  def +=(v: Float) {
    var y = v - c
    var t = sum + y
    c = (t - sum) - y
    sum = t
  }
}

trait POJOKahanSummerData extends KahanSummerData {
  var sum = 0f
  var c = 0f
}

@MutableByteBufferBacked[KahanSummerData]class BBKahanSummerData(var bb: ByteBuffer, var position:Int)

class BBKahanSummer(bb: ByteBuffer, pos: Int) extends BBKahanSummerData(bb, pos) with KahanSummer
class POJOKahanSummer extends POJOKahanSummerData with KahanSummer