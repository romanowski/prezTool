package pl.typosafe.preztool.pages

import java.io.{StringWriter, InputStreamReader, FileReader}
import java.nio.file.{Files, Path}
import java.util.Collections
import collection.JavaConverters._


import com.github.mustachejava.{Mustache, DefaultMustacheFactory}

trait Renderable{
  def renderLike: Any
}

object Renderable{
  implicit class RString(s: String) extends Renderable{
    override def renderLike: Any = s
  }

  implicit class RMap(map: Map[String, Renderable]) extends Renderable{
    override def renderLike: Any = map.mapValues(_.renderLike).asJava
  }

  implicit class RList(map: List[Renderable]) extends Renderable{
    override def renderLike: Any = map.map(_.renderLike).asJava
  }
}

abstract class Page(val name: String,
           val templateName: String,
           val baseDir: Path){
  def data: Map[String, Renderable]

  def rmap(entries: (String, Renderable)*): Map[String, Renderable] =
    Map(entries:_*)

  def rlist(entries: Renderable*): List[Renderable] =
    List(entries:_*)

}

object Page {
  private val factory = new DefaultMustacheFactory()

  def readTemplate(name: String, templateName: String, baseDir: Path): Mustache = {
    val specialTemplate = baseDir.resolve(s"$templateName.html")

    val reader = if (Files.exists(specialTemplate)) {
      new FileReader(specialTemplate.toFile)
    } else {
      val classpathEntry =
        getClass.getClassLoader.getResourceAsStream(s"pl/typosafe/preztool/pages/$templateName.html")

      assert(classpathEntry != null)
      new InputStreamReader(classpathEntry)
    }

    factory.compile(reader, name)
  }

  def render(p: Page): String = {
    val template = readTemplate(p.name, p.templateName, p.baseDir)
    val sw = new StringWriter()

    template.execute(sw, p.data.renderLike)
    sw.toString
  }
}