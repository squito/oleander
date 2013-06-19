package com.imranrashid.oleander

/**
 * A collection w/ array like properties -- O(1) access by index, you can mutate the elements, but can't change the size
 */
trait ArrayLike[@specialized T] {
  def apply(idx: Int) : T
  def update(idx: Int, value: T)
  def length: Int
  def size: Int = length
}

/**
 * just for testing / profiling -- delete this eventually
 */
@deprecated(message="dont use", since="always")
trait FloatArray {
  def apply(idx: Int) : Float
  def update(idx: Int, value: Float)
  def length: Int
  def size: Int
}
