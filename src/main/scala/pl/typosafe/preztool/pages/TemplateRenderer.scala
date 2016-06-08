package pl.typosafe.preztool.pages

import java.io.{StringWriter, InputStreamReader, FileReader}
import java.nio.file.{Files, Path}

import com.github.mustachejava.{DefaultMustacheFactory, Mustache}
import pl.typosafe.preztool.pages.Page._
import spray.http.Renderable
import collection.JavaConverters._


object TemplateRenderer {

  def rmap(entries: (String, Renderable)*): Map[String, Renderable] =
    Map(entries: _*)

  def rlist(entries: Renderable*): List[Renderable] =
    List(entries: _*)

  trait Renderable {
    def renderLike: Any
  }

  object Renderable {

    implicit class RString(s: String) extends Renderable {
      override def renderLike: Any = s
    }

    implicit class RMap(map: Map[String, Renderable]) extends Renderable {
      override def renderLike: Any = map.mapValues(_.renderLike).asJava
    }

    implicit class RList(map: List[Renderable]) extends Renderable {
      override def renderLike: Any = map.map(_.renderLike).asJava
    }
  }

  private val factory = new DefaultMustacheFactory()


  private def readTemplate(templateName: String, baseDir: Option[Path] = None): Mustache = {
    val specialTemplate =
      baseDir
        .map(_.resolve(s"$templateName.html"))
        .filter(Files.exists(_))
        .map(p => new FileReader(p.toFile))


    val reader = specialTemplate.getOrElse {
      val classpathEntry =
        getClass.getClassLoader.getResourceAsStream(s"pl/typosafe/preztool/pages/$templateName.html")

      assert(classpathEntry != null)
      new InputStreamReader(classpathEntry)
    }

    factory.compile(reader, templateName)
  }

  def renderTemplate(templateName: String, data: Map[String, Renderable], baseDir: Option[Path] = None) ={
    val template = readTemplate(templateName, baseDir)
    val sw = new StringWriter()

    template.execute(sw, data.renderLike)
    sw.toString
  }
}
