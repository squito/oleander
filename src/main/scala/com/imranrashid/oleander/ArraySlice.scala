package com.imranrashid.oleander

import java.nio.ByteBuffer

/**
 *
 */
class FloatArraySlice(var bb: ByteBuffer) extends ByteBufferBacked {
  var floatBuffer = bb.asFloatBuffer()
  override def initFrom(otherBB: ByteBuffer) {this.bb = otherBB}
  override def asByteBuffer(): ByteBuffer = {bb}
  def apply(idx: Int): Float = floatBuffer.get(idx)
  def update(idx: Int, v: Float) {floatBuffer.put(idx, v)}
}
