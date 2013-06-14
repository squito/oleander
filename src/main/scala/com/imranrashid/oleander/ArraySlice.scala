package com.imranrashid.oleander

import java.nio.ByteBuffer

/**
 * Simple way to read an array of floats from a ByteBuffer.
 *
 * Some simple microbenchmarks show this is about half as fast reading from a true
 * float array (from a test that just sums the array).
 */
class FloatArraySlice(var bb: ByteBuffer) extends ByteBufferBacked {
  var floatBuffer = bb.asFloatBuffer()
  override def initFrom(otherBB: ByteBuffer) {this.bb = otherBB}
  override def asByteBuffer(): ByteBuffer = {bb}
  def apply(idx: Int): Float = floatBuffer.get(idx)
  def update(idx: Int, v: Float) {floatBuffer.put(idx, v)}
}
