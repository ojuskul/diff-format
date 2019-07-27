object DiffUnitTest extends App {

  val differ: diff_match_patch = new diff_match_patch

  differ.Diff_Timeout = 0.015f

  val op = differ.diff_main("This is first string","This is second string",true).toArray

  op
}
