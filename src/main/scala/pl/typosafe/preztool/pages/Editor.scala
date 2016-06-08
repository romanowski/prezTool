package pl.typosafe.preztool.pages

import java.io.{File, FileWriter}
import java.nio.file.Paths

import scala.io.Source
import TemplateRenderer._

case class Editor(index: Index) {

  private def editedFile(path: String): File = {
    val file = index.basePath.resolve(path).toFile

    if (path.endsWith(".md") && file.exists() && file.isFile) {
      file
    } else {
      ???
    }
  }

  def read(path: String): String = {
    val file = editedFile(path)
    val content = Source.fromFile(file).mkString

    val data = rmap(
      "content" -> content
    )

    TemplateRenderer.renderTemplate("editor", data)

  }

  def save(path: String, content: String): Unit = {
    val fileWriter = new FileWriter(editedFile(path))
    try {
      fileWriter.write(content)
    } finally {
      fileWriter.close()
    }
  }

}
