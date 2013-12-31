package com.imranrashid.oleander.demos.dataread

import com.quantifind.sumac.{ArgMain, FieldArgs}
import java.io.File
import com.imranrashid.oleander.BB2Itr
import java.nio.ByteBuffer

/**
 *
 */
object ReadAndSum extends ArgMain[ReadAndSumArgs] {
  def main(args: ReadAndSumArgs) {
    time("regular")(regularSum(args))
    time("bb kahan")(bbKahanSum(args))
    time("pojo kahan")(pojoKahanSum(args))
    time("bb kahan")(bbKahanSum(args))
  }

  def time(name: String)(f: => Unit) {
    val start = System.nanoTime()
    f
    val end = System.nanoTime()
    println(name + "\t" + (end - start).toDouble / 1e9)
  }

  def regularSum(args: ReadAndSumArgs) {
    val data = BB2Itr.fromMemMappedFile[DataPointIm](args.inputFile)
    val bucketSums = new Array[Float](args.nBuckets)
    data.foreach{dp =>
      bucketSums(dp.bucket) += dp.value
    }
  }

  def bbKahanSum(args: ReadAndSumArgs) {
    val data = BB2Itr.fromMemMappedFile[DataPointIm](args.inputFile)
    val bbsum = ByteBuffer.allocate(args.nBuckets * 8)
    val sums = BB2Itr.indexedBB2[BBKahanSummer](bbsum)
    data.foreach{dp =>
      sums(dp.bucket) += dp.value
    }
  }

  def pojoKahanSum(args: ReadAndSumArgs) {
    val data = BB2Itr.fromMemMappedFile[DataPointIm](args.inputFile)
    val sums = new Array[POJOKahanSummer](args.nBuckets)
    (0 until args.nBuckets).foreach{idx => sums(idx) = new POJOKahanSummer}
    data.foreach{dp =>
      sums(dp.bucket) += dp.value
    }
  }


}


class ReadAndSumArgs extends FieldArgs {
  var inputFile: File = _
  var nBuckets: Int = 1e4.toInt
}