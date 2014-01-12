package com.imranrashid.oleander

import java.io.{RandomAccessFile, File}
import java.nio.channels.FileChannel.MapMode
import scala.reflect.ClassTag
import java.nio.{MappedByteBuffer, ByteBuffer}

object ByteBufferBackedItr {
  def fromMemMappedFile[T <: ByteBufferBacked: ClassTag](file: File, load: Boolean = false): Iterable[T] = {
    val (mbb, maxBytes) = mmap(file, load)
    val t = makeByteBufferBacked[T](mbb)
    new Iterable[T]{
      def iterator = {
        (0 until maxBytes.toInt by t.numBytes).iterator.map{ idx =>
          t.setBuffer(mbb, idx)
          t
        }
      }
    }
  }

  def byteBufferToIterable[T <: ByteBufferBacked: ClassTag](bb: ByteBuffer): Iterable[T] = {
    val (t, range) = bbWithRange[T](bb)
    new Iterable[T]{
      def iterator = {
        range.iterator.map{ idx =>
          t.setBuffer(bb, idx)
          t
        }
      }
    }
  }

  def bbAsTraversable[T <: ByteBufferBacked: ClassTag](bb: ByteBuffer, obj: T)= {
    new ByteBufferBackedTraversable(bb, 0, bb.limit(), obj)
  }

  def bbWithRange[T <: ByteBufferBacked: ClassTag](bb: ByteBuffer): (T, Range) = {
    val t = makeByteBufferBacked[T](bb)
    (t, (0 until bb.limit() by t.numBytes)) //TODO shouldn't just be 0 until limit(), should have a slice
  }

  class ByteBufferBackedTraversable[T <: ByteBufferBacked](bb: ByteBuffer, start: Int, length: Int, o: T) extends Traversable[T] {
    def foreach[R](f: T => R){
      (start to (start + length) by o.numBytes).foreach{idx =>
        o.setBuffer(bb, idx)
        f(o)
      }
    }
  }

  def makeByteBufferBacked[T <: ByteBufferBacked: ClassTag](bb: ByteBuffer): T = {
    val cls = implicitly[ClassTag[T]].runtimeClass
    val ctor = cls.getConstructor(Array(classOf[ByteBuffer], Integer.TYPE): _*)
    ctor.newInstance(Array(bb, new java.lang.Integer(0)): _*).asInstanceOf[T]
  }

  def indexedByteBufferBacked[T <: ByteBufferBacked: ClassTag](bb: ByteBuffer): IndexedBb2[T] = {
    val t = makeByteBufferBacked[T](bb)
    new IndexedBb2[T](bb, 0, bb.limit(), t)
  }

  def mmap(file: File, load: Boolean = true): (MappedByteBuffer, Long) = {
    val raf = new RandomAccessFile(file, "rw")
    val fc = raf.getChannel
    val maxBytes = file.length
    val mbb = fc.map(MapMode.READ_WRITE, 0, maxBytes)
    if (load) {
      mbb.load()
    }
    (mbb, maxBytes)
  }

}

class IndexedBb2[T <: ByteBufferBacked](bb: ByteBuffer, start: Int, length: Int, val t: T) {

  def apply(idx: Int): T = {
    t.setBuffer(bb, start + idx * t.numBytes)
    t
  }
}
