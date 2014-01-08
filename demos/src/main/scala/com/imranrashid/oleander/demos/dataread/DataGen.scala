package com.imranrashid.oleander.demos.dataread

import com.quantifind.sumac.{ArgMain, FieldArgs}
import java.io.{RandomAccessFile, File}
import java.nio.{ByteBuffer, MappedByteBuffer}
import java.nio.channels.FileChannel.MapMode
import com.imranrashid.oleander.macros.{MutableByteBufferBacked, ByteBufferBacked}
import scala.util.Random
import com.quantifind.sumac.validation.Required

object DataGen extends ArgMain[DataGenArgs] {
  def main(args: DataGenArgs) {
    val numBytes = args.nPoints * 8
    val raf = new RandomAccessFile(args.outputFile, "rw")
    val fc = raf.getChannel
    val mbb = fc.map(MapMode.READ_WRITE, 0, numBytes)
    val dp = new DataPointMut(mbb, 0)
    val rng = new Random
    (0 until args.nPoints).foreach{idx =>
      val v = rng.nextFloat()
      val bucket = rng.nextInt(args.nBuckets)
      dp.setBuffer(mbb, idx * 8)
      dp.bucket = bucket
      dp.value = v
    }
    fc.close()
    raf.close()
  }
}

class DataGenArgs extends FieldArgs {
  var nPoints = 1e6.toInt
  var nBuckets = 1e4.toInt
  @Required
  var outputFile : File = _
}

trait DataPoint {
  def bucket: Int
  def value: Float
}

@ByteBufferBacked[DataPoint] class DataPointIm(var bb: ByteBuffer, var position: Int)
@MutableByteBufferBacked[DataPoint] class DataPointMut(var bb: ByteBuffer, var position: Int)

class DataPointPOJO(val bucket: Int, val value: Float) extends DataPoint

object EchoData extends ArgMain[EchoDataArgs] {
  def main(args: EchoDataArgs) {

    val raf = new RandomAccessFile(args.file, "rw")
    val fc = raf.getChannel
    val maxBytes = math.min(args.file.length, args.maxPoints * 8).toInt
    val mbb = fc.map(MapMode.READ_WRITE, 0, maxBytes)
    val dp = new DataPointIm(mbb, 0)
    (0 until maxBytes by 8).foreach { offset =>
      dp.setBuffer(mbb, offset)
      println(s"$offset\t${dp.bucket}\t${dp.value}")
    }

  }
}

class EchoDataArgs extends FieldArgs {
  var file: File = _
  var maxPoints = 500
}