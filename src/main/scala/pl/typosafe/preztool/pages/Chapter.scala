package pl.typosafe.preztool.pages

import java.nio.file.Path

import scala.collection.JavaConversions._

case class Chapter(chapterName: String, path: Path, parent: Index)
  extends Page(chapterName, "chapter-template", path) {

  val orderRegexp = "\\d+\\."

  def fileNameToTitle(fileName: String): String = {
    fileName.replaceFirst(orderRegexp, "").replaceAll("_", " ").dropRight(3).capitalize.trim
  }

  case class Subsection(title: String, path: String) extends Renderable{
    override def renderLike: Any = this
  }

  lazy val subsections: List[Subsection] = {
    val markdowns = path.toFile.list().filter(_.endsWith(".md"))

    val (desc, nonDesc) = markdowns.partition("description.md" ==)


    (desc ++ nonDesc.sorted).map(fileName =>
      new Subsection(fileNameToTitle(fileName), s"$chapterName/$fileName")).toList
  }

  override def data: Map[String, Renderable] = {
    parent.data ++ rmap(
      "name" -> chapterName,
      "subsections" -> subsections
    )
  }
}