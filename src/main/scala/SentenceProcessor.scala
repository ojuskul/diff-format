import diff_match_patch.Operation

case class Sentence
(
  text: String,
  props: Map[String,Array[idx]]
)

case class idx
(
  start: Int,
  end: Int
)

object SentenceProcessor {

  def offsetDiffProps(old: Sentence, `new`: Sentence, diff: Array[diff_match_patch.Diff]):Sentence = {

    //hold current index of the old/new text
    var oldIndex: Int = -1
    var newIndex: Int = -1
    var runIndex: Int = -1

    //hold the diff properties of new sentence
    var insertIdx: Array[idx] = Array[idx]()
    var deleteIdx: Array[idx] = Array[idx]()
    var equalIdx: Array[idx] = Array[idx]()

    //hold the new sentence
    var newText: String = ""
    var newProps: Array[(String,Array[idx])] = Array[(String,Array[idx])]()

    for(obj <- diff){
      if(obj.operation == Operation.EQUAL){
        //copy over properties from new text
        newProps = newProps ++
          `new`.props.toArray.filter( f => (f._2.filter(id => (id.start > newIndex && id.end <= newIndex + obj.text.length))).nonEmpty) //filter out all keys for matching index
          .map(m => (m._1, m._2.filter(f => (f.start > newIndex && f.end <= newIndex + obj.text.length)))) //filter the properties that fall within index
          .map(m => (m._1, m._2.map(id => idx(id.start + (runIndex - newIndex), id.end + (runIndex - newIndex)))))  //offset the properties

        newIndex += obj.text.length
        oldIndex += obj.text.length

        equalIdx = equalIdx ++ Array(idx(runIndex,runIndex + obj.text.length))
      }else if(obj.operation == Operation.DELETE){
        newProps = newProps ++
          old.props.toArray.filter( f => (f._2.filter(id => (id.start > oldIndex && id.end <= oldIndex + obj.text.length))).nonEmpty) //filter out all keys for matching index
            .map(m => (m._1, m._2.filter(f => (f.start > oldIndex && f.end <= oldIndex + obj.text.length)))) //filter the properties that fall within index
            .map(m => (m._1, m._2.map(id => idx(id.start + (runIndex - oldIndex), id.end + (runIndex - oldIndex)))))  //offset the properties

        oldIndex += obj.text.length

        deleteIdx = deleteIdx ++ Array(idx(runIndex,runIndex + obj.text.length))
      }else if(obj.operation == Operation.INSERT){
        newProps = newProps ++
          `new`.props.toArray.filter( f => (f._2.filter(id => (id.start > newIndex && id.end <= newIndex + obj.text.length))).nonEmpty) //filter out all keys for matching index
            .map(m => (m._1, m._2.filter(f => (f.start > newIndex && f.end <= newIndex + obj.text.length)))) //filter the properties that fall within index
            .map(m => (m._1, m._2.map(id => idx(id.start + (runIndex - newIndex), id.end + (runIndex - newIndex)))))  //offset the properties

        newIndex += obj.text.length

        insertIdx = insertIdx ++ Array(idx(runIndex,runIndex + obj.text.length))
      }
      newText += obj.text
      runIndex += obj.text.length
    }

    //add insertions to props
    if(insertIdx.nonEmpty){
      newProps = newProps ++ Array(("diff_insert",insertIdx))
    }
    //add deletions to props
    if(deleteIdx.nonEmpty){
      newProps = newProps ++ Array(("diff_delete",deleteIdx))
    }
    //add equals to props
    if(equalIdx.nonEmpty){
      newProps = newProps ++ Array(("diff_equal",equalIdx))
    }

    //create the return sentence
    Sentence(
      newText,
      newProps.map(_._1).distinct.map( m =>
      (m,newProps.filter(_._1 == m).map(_._2).flatten)).toMap
    )
  }
}
