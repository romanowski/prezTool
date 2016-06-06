package pl.typosafe.prezTool.pages

import java.nio.file.{Files, Path}

import shapeless.{HNil, ::}

case class Index(basePath: Path) extends Page {
  override def content(): String = "alaMAkota"

  def chapter(name: String :: HNil): Option[Chapter :: HNil] = {
    val chapterPath = basePath.resolve(name.head)

    if(Files.isDirectory(chapterPath))
      Option(Chapter(chapterPath) :: HNil)
    else None
  }
}
