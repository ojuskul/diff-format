import scala.collection.JavaConverters

object OffsetTest extends App {

  val differ: diff_match_patch = new diff_match_patch
  differ.Diff_Timeout = 0.015f

  val old = Sentence(
    "This is first string",
    Map("bold" -> Array(idx(8,12)),"underline" -> Array(idx(0,3)))
  )
  val `new` = Sentence(
    "This is second string",
    Map("italics" -> Array(idx(8,13),idx(15,20)),"underline" -> Array(idx(0,3)))
  )

  val diff = JavaConverters.asScalaIterator(differ.diff_main(old.text,`new`.text,true).iterator).toArray
  val offsetOp = SentenceProcessor.offsetDiffProps(old,`new`,diff)
  offsetOp
}
