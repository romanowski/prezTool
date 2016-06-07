package pl.typosafe.preztool.pages

import java.nio.file.{Files, Path}

import shapeless.{HNil, ::}
import collection.JavaConversions._

case class Index(basePath: Path) extends Page("index", "index-template", basePath) {

  def chapter(name: String :: HNil): Option[Chapter :: HNil] = {
    val chapterPath = basePath.resolve(name.head)

    if(Files.isDirectory(chapterPath))
      Option(Chapter(name.head, chapterPath, this) :: HNil)
    else None
  }

  override def data: Map[String, Renderable] = Map{
    "name" -> "index"
  }
}