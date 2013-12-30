package com.imranrashid.oleander

import java.nio.ByteBuffer

trait BB2 {
  def setBuffer(bb: ByteBuffer, pos: Int)
  def numBytes: Int
}
