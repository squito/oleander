package com.imranrashid.oleander;

import java.nio.ByteBuffer;

public interface ByteBufferBacked {
  /**
   * get the length of this record, in bytes
   */
  public int numBytes();
  
  /**
   * change this record to read & write a different location in memory.
   * Note that this doesn't do any copying, it just moves the location
   * it "points" to
   */
  public void setBuffer(ByteBuffer bb, int pos);
}
  
