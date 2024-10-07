package prg1.support.world

import java.awt.image.BufferedImage
import java.awt.{Color => JColor, Graphics2D}

import scala.swing.Swing._
import scala.swing.event._
import scala.swing.{Frame, Panel}

import prg1.graphics.color.Color

case class Pos(x: Int, y: Int)

abstract class World(tick_ms: Int) {
  def _tick_ms: Int = tick_ms

  private val _img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
  protected var g: Graphics2D = _img.createGraphics()

  protected def draw(g: Graphics2D): Unit = {
    this.g = g
    draw()
  }
  protected def draw(): Unit = ()
  protected final def drawCircle(p: Pos, r: Int, c: Color) = {
    g.setColor(c.c)
    g.drawOval(p.x - r, p.y - r, r + r, r + r)
  }
  protected final def drawDisk(p: Pos, r: Int, c: Color) = {
    g.setColor(c.c)
    g.fillOval(p.x - r, p.y - r, r + r, r + r)
  }
  protected final def drawLine(start: Pos, end: Pos, c: Color) = {
    g.setColor(c.c)
    g.drawLine(start.x, start.y, end.x, end.y)
  }
  protected final def drawRect(p: Pos, w: Int, h: Int, c: Color) = {
    g.setColor(c.c)
    g.fillRect(p.x, p.y, w, h)
  }
  protected final def drawString(p: Pos, message: String) = {
    g.setColor(JColor.black)
    g.drawString(message, p.x, p.y)
  }

  protected def tick():            World = World.currentWorld
  protected def keyTyped(c: Char): World = World.currentWorld

  private def defaultMouseAction[T <: MouseEvent](message: String, e: T): World = {
    println(s"$message: ${e.point}")
    World.currentWorld
  }
  private def mouseAction[T <: MouseEvent](e: T, handle: (Pos) => World): World = handle(Pos(e.point.x, e.point.y))

  protected def mouseClicked(p: Pos): World = World.currentWorld

  protected def mouseClicked(e:  MouseClicked): World = defaultMouseAction("Mouse clicked", e)
  protected def mousePressed(e:  MousePressed): World = defaultMouseAction("Mouse pressed", e)
  protected def mouseDragged(e:  MouseDragged): World = defaultMouseAction("Mouse dragged", e)
  protected def mouseReleased(e: MouseReleased): World = defaultMouseAction("Mouse released", e)

  def dooms_day(message: String): World = {
    World.DoomsDay
  }
}

class DoomsDay extends World(0) {}
class SimpleWorld extends World(1000) {}

object World {
  val debug = false

  val DoomsDay: DoomsDay = new DoomsDay
  var currentWorld: World = new SimpleWorld
  var currentThread: Thread = Thread.currentThread

  var hasCanvas = false

  val canvas: Panel = new Panel {
    background = new JColor(255, 255, 255)
    preferredSize = (100, 100)
    focusable = true
    listenTo(mouse.clicks, mouse.moves, keys)

    override def paintComponent(g: Graphics2D) = {
      super.paintComponent(g)

      val w = size.width.toInt
      val h = size.height.toInt
      g.setColor(new JColor(255, 255, 255))
      g.fillRect(0, 0, w, h)

      currentWorld.draw(g)
    }

    def handle(handle_event: () => World): Unit = {
      currentWorld = handle_event()
      currentThread.interrupt()
    }

    reactions += {
      case e: MouseClicked => handle(() => currentWorld.mouseClicked(e))
      case e: MousePressed => handle(() => currentWorld.mousePressed(e))
      case e: MouseDragged => handle(() => currentWorld.mouseDragged(e))
      case e: MouseReleased => handle(() => currentWorld.mouseReleased(e))
      case KeyTyped(source, c, y, z) => {
        println((c, y, z))
        handle(() => currentWorld.keyTyped(c))
      }
      case _: FocusLost => repaint()
    }
  }

  private def driver(world: World): Unit = {
    println(s"canvas size: ${canvas.size}")
    currentThread = Thread.currentThread
    currentWorld = world
    while (true) {
      var interrupted = false
      if (debug) println(currentWorld)
      try {
        Thread.sleep(currentWorld._tick_ms)
      } catch { case e: InterruptedException => { interrupted = true } }
      try {

        if (!interrupted) currentWorld = currentWorld.tick()
        if (currentWorld == DoomsDay) {
          println("End of the world: Doom's day has arrived.")
          scala.sys.exit()
        }
        if (hasCanvas) canvas.repaint()
      } catch {
        case e => {
          e.printStackTrace()
          scala.sys.exit()
        }
      }
    }
  }

  def bigbang(world: World): Unit = {
    driver(world)
  }

  def bigbang2d(world: World, window_title: String, width: Int, height: Int): Unit = {
    canvas.preferredSize = (width, height)
    hasCanvas = true
    canvas.repaint()

    val frame = new Frame {
      title = window_title
      contents = canvas
      centerOnScreen()
      open()
    }

    bigbang(world)
  }

  @main def run = bigbang(new SimpleWorld)
}