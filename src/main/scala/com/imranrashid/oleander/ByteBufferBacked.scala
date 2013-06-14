package com.imranrashid.oleander

import java.nio.ByteBuffer
import java.io.{ObjectInput, ObjectOutput, Externalizable}

/**
 * This trait indicates that an instance of this class can store all
 * its (non-transient) data in a ByteBuffer.  This way we can try to
 * make serialization & deserialization as close to a no-op as we can,
 * when possible.
 */
trait ByteBufferBacked extends Externalizable {
  /**
   * initialize this data structure from the given byte buffer.  The byte
   * buffer's position is set tot he end of this instance's data, so the
   * next object can be read from it.
   *
   * Implementations are expected to be O(1) -- that is, there should not
   * be any copying of data out of the byte buffer, it should just be used as is.
   */
  def initFrom(bb: ByteBuffer)
  
  /**
   * Get a ByteBuffer with all the data for this object.  The position & limit should
   * be set appropriately to just cover the data for this object.
   *
   * Implementations are expected to be O(1) -- no copying into a new byte buffer, the
   * data should already be ByteBuffer compatible.
   */
  def asByteBuffer(): ByteBuffer

  def writeExternal(out: ObjectOutput) {
    //unfortunately, the externalizable interface requires a copy ...
    val bb = asByteBuffer()
    out.writeInt(bb.limit() - bb.position())
    out.write(bb.array(), bb.arrayOffset(), bb.limit())
  }

  def readExternal(in: ObjectInput) {
    //unfortunately, the externalizable interface requires a copy ...
    val size = in.readInt()
    val bytes = new Array[Byte](size)
    in.readFully(bytes)
    initFrom(ByteBuffer.wrap(bytes))
  }
}
