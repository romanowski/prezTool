package pl.typosafe.preztool

import java.awt.event.{WindowEvent, WindowAdapter}
import java.io.File
import javax.swing.{JLabel, SwingUtilities, Box, JFrame}

import akka.actor.ActorSystem
import spray.routing.Directive.Empty
import spray.routing.SimpleRoutingApp

import pages._


object Main extends App with SimpleRoutingApp {

  private implicit def funcToRunnable[T](f: () => T): Runnable = new Runnable {
    override def run(): Unit = f()
  }

  implicit val system = ActorSystem()

  var index = Index(new File("testProj").getAbsoluteFile.toPath)

  private def setupGui(): Unit = {
    val frame = new JFrame("My Prez Tool")
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

    val layout = Box.createVerticalBox()

    val row = Box.createHorizontalBox()
    row.add(new JLabel("Listen in:"))

    layout.add(row)

    layout.add(new JLabel("AAsasasa"))

    frame.getContentPane.add(layout)

    frame.pack()
    frame.setVisible(true)

    frame.addWindowListener(new WindowAdapter {
      override def windowClosed(e: WindowEvent): Unit = system.terminate()
    })
  }

  //SwingUtilities.invokeLater(setupGui _)

  import spray.http.MediaTypes._

  def asHTML(content: String) = respondWithMediaType(`text/html`)(complete(content))

  startServer(interface = "localhost", port = 8888) {
    pathEndOrSingleSlash {
      get {
        asHTML(Page.render(index))
      }
    } ~
      pathPrefix("edit") {
        (pathEndOrSingleSlash | path("index.html")) {
          parameter('path) { editPath =>
            get {
              asHTML(index.editor.read(editPath))
            } ~
            post {
              entity(as[String]) { data =>
                index.editor.save(editPath, data)
                complete("Saved")
              }
            }
          }
        } ~
          getFromResourceDirectory("pl/typosafe/preztool/editor")
      } ~
      pathPrefix(Segment.hflatMap(index.chapter)) { chapter =>
        pathEndOrSingleSlash {
          get {
            asHTML(Page.render(chapter))
          }
        } ~
          path(Rest) { fileName =>
            getFromFile(chapter.subsection(fileName).toFile)
          } ~
          getFromResourceDirectory("pl/typosafe/preztool/assets")
      } ~
      getFromResourceDirectory("pl/typosafe/preztool/assets")

  }
}
