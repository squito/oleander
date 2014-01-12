package com.imranrashid.oleander;

import java.nio.ByteBuffer;

/**
 * A marker trait, indicating that the length of this records in bytes
 * is *entirely* determined by its type.  It does not contain any
 * variable length fields
 */
public interface FixedLengthBBB extends ByteBufferBacked {}
