package pl.typosafe.prezTool.pages

import java.nio.file.Path


case class Chapter(path: Path) extends Page {
  override def content(): String = path.toString()
}
