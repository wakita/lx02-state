package prg1.lx02.timer2


import prg1.support.world._
import prg1.graphics.color.Color
import java.awt.{Color => JColor, Font, Graphics2D}

object Timer2 {
  private val ARIAL60B = new Font("Arial", Font.BOLD, 60)
}

/**
 * TimerWorld2の実装では、経過した時間を10ミリ秒単位の自然数として保存している。
 * つまり、"保存されている経過時間の値が105の場合、1秒"+50ミリ秒が経過したことを表す。
 * なお、1秒 = 1000ミリ秒、10ミリ秒の100倍が1秒。
 **/

case class Timer2(sec: Int, subsec: Int, tick_ms: Int) extends World(tick_ms) {
  import Timer2._

  /**
   * draw は画面の描画を担当する関数。
   * tick を初めとするイベント処理のたびに自動的に呼ばれる。
   * ここでは、経過時間を描画している。
   **/
  override def draw() = {
    val s = World.canvas.size

    g.setFont(ARIAL60B)
    // 画面に経過時間を表示
    drawString(Pos(s.width / 2, s.height / 2), f"$sec.$subsec%02d")
  }

  /**
   * tick関数は bigBang 関数で指定された時間の刻み幅ごとに自動的に呼び出される。
   * ここでは経過時間を加算して次のワールドを構成している。
   **/
  override def tick(): World = {
    if (subsec + 1 == 100) { Timer2(sec + 1, 0, tick_ms) }
    else { Timer2(sec, subsec + 1, tick_ms) }
  }
}

// 初期ワールドの構成。ここから世界が始まる。
@main def run = World.bigbang2d(Timer2(0, 0, 10), "Timer 2", 300, 100)
