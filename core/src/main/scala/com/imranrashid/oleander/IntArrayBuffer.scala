package com.imranrashid.oleander

import java.nio.ByteBuffer

/**
 * Simple way to read an array of floats from a ByteBuffer.
 *
 * Some simple microbenchmarks show this is about 1/5 as fast reading from a true
 * int array (from a test that just sums the array).
 *
 * This expects to read ints from the entire given byte buffer.  If you want to read from part of a byte buffer,
 * first create a slice of the byte buffer covering the relevant range.
 */
class IntArrayBuffer(var bb: ByteBuffer) extends ByteBufferBacked with ArrayLike[Int] {
  var intBuffer = bb.asIntBuffer()
  def setBuffer(bb: ByteBuffer, pos: Int) {
    require(pos == 0) //TODO
    this.bb = bb
    intBuffer = bb.asIntBuffer()
  }
  def apply(idx: Int): Int = intBuffer.get(idx)
  def update(idx: Int, v: Int) {intBuffer.put(idx, v)}
  def length = intBuffer.limit
  def numBytes = 4
}
