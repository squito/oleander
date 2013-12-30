package com.imranrashid.oleander

import java.io.{RandomAccessFile, File}
import java.nio.channels.FileChannel.MapMode
import scala.reflect.ClassTag
import java.nio.ByteBuffer

/**
 *
 */
object BB2Itr {
  def fromMemMappedFile[T <: BB2: ClassTag](file: File): Iterator[T] = {
    //TODO auto-create a T
    val cls = implicitly[ClassTag[T]].runtimeClass
    val ctor = cls.getConstructor(Array(classOf[ByteBuffer], Integer.TYPE): _*)
    val raf = new RandomAccessFile(file, "rw")
    val fc = raf.getChannel
    val maxBytes = file.length
    val mbb = fc.map(MapMode.READ_WRITE, 0, maxBytes)
    val t = ctor.newInstance(Array(mbb, new java.lang.Integer(0)): _*).asInstanceOf[T]
    (0 until maxBytes.toInt by t.numBytes).iterator.map{ idx =>
      t.setBuffer(mbb, idx)
      t
    }
  }
}
