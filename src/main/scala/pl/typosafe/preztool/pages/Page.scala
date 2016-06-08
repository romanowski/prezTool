package pl.typosafe.preztool.pages

import java.io.{StringWriter, InputStreamReader, FileReader}
import java.nio.file.{Files, Path}
import TemplateRenderer.Renderable

import com.github.mustachejava.{Mustache, DefaultMustacheFactory}

abstract class Page(val name: String,
                    val templateName: String,
                    val baseDir: Path) {
  def data: Map[String, Renderable]

}

object Page {
  def render(p: Page): String =
    TemplateRenderer.renderTemplate(p.templateName,p.data, Some(p.baseDir))
}