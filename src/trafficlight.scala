package prg1.lx02.traffic_light

// bug: 最初の2秒赤信号が点灯しない。canvas.repaint のタイミングエラーか？

import prg1.support.world._
import prg1.graphics.color.Color
import java.awt.{Color => JColor, Graphics2D}

val Red    = Color(JColor.red)
val Green  = Color(JColor.green)
val Yellow = Color(JColor.yellow)

case class Light(color: Color, tick_ms: Int) extends World(tick_ms) {
  /**
   * 信号を描画する。円を塗り潰しているだけ。
   **/
  override def draw() = {
    val s = World.canvas.size
    val x = (s.width / 2).toInt
    val y = (s.height / 2).toInt
    val r = scala.math.min(x, y) * 4 / 5
    drawDisk(Pos(x, y), r, color)
  }

  /**
   * 信号の状態の遷移
   **/
  override def tick(): World = {
    val c = color match {
      case Red => Green
      case Green => Yellow
      case Yellow => Red
    }
    Light(c, 2000)
  }
}

@main def run = World.bigbang2d(new Light(Red, 2000), "Traffic Light", 300, 300)