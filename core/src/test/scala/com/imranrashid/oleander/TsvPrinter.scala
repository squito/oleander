package com.imranrashid.oleander

import ichi.bench.Thyme.Benched
import java.io.{PrintWriter, File}
import ichi.bench.Thyme


/**
 *
 */
class TsvPrinter(val th: Thyme, val file: File) {
  val out = new PrintWriter(file)
  out.println("title\truntime")

  def addToTsv(benched: Benched) {
    benched.runtimeResults.data.value.foreach{runtime =>
      out.println(benched.title + "\t" + (runtime / benched.runtimeEffort))
    }
  }

  def close() {out.close()}


  def tsvBench[A](f: => A, effort: Int = 1, title: String = "") = {
    val br = Thyme.Benched.empty
    br.title = title
    val ans = th.bench(f)(br, effort = effort)
    println(br)
    addToTsv(br)
    ans
  }

  def showRCommand = println(
    """data <- read.table("""" + file.getAbsolutePath +
      """", sep="\t",h=T)
        |# you might want to reorder the plot with something like:
        |# data$title = factor(data$title, levels = c(<desired order>), ordered=T)
        |boxplot(runtime ~ title, data, ylab="seconds", ylim=c(0,max(data$runtime)))
        |grid()
      """.stripMargin
  )
}
