package com.imranrashid.oleander

import java.nio.ByteBuffer

/**
 * Simple way to read an array of floats from a ByteBuffer.
 *
 * Some simple microbenchmarks show this is about half as fast reading from a true
 * float array (from a test that just sums the array).
 *
 * This expects to read floats from the entire given byte buffer.  If you want to read from part of a byte buffer,
 * first create a slice of the byte buffer covering the relevant range.
 */
class FloatArrayBuffer(var bb: ByteBuffer) extends ByteBufferBacked with ArrayLike[Float] {
  var floatBuffer = bb.asFloatBuffer()
  def setBuffer(bb: ByteBuffer, pos: Int) {
    //TODO handle non-zero pos
    require(pos == 0)
    this.bb = bb
    floatBuffer = bb.asFloatBuffer()
  }
  def apply(idx: Int): Float = floatBuffer.get(idx)
  def update(idx: Int, v: Float) {floatBuffer.put(idx, v)}
  def length = floatBuffer.limit
  def numBytes = 4
}
