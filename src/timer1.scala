package prg1.lx02.timer1

import prg1.support.world._
import prg1.graphics.color._
import java.awt.{Color => JColor, Font}

object Timer1 {
  val Black    = Color(JColor.black)
  val White    = Color(JColor.white)
  private val ARIAL60B = new Font("Arial", Font.BOLD, 60)
}

/**
 * TimerWorld1の時間の表現は、経過時間を10ミリ秒単位の自然数としている。
 * これは TimerWorld1(t: Int) において引数 t で表されている。
 * つまり、保存されている経過時間の値が105の場合、1秒+50ミリ秒が経過したことを表す。
 * なお、1秒 = 1000ミリ秒、10ミリ秒の100倍が1秒。
 **/
case class Timer1(t: Int, tick_ms: Int) extends World(tick_ms) {
  import Timer1._

  /**
    * draw は画面の描画を担当する関数。
    * tick を初めとするイベント処理のたびに自動的に呼ばれる。
    * ここでは、経過時間を描画している。
    */
  override def draw() = {
    // 画面に経過時間を表示
    g.setFont(ARIAL60B)
    val s = World.canvas.size
    drawString(Pos(s.width / 2, s.height / 2), f"${0.01*t}%.02f")
  }

  /**
   * tick関数は tick_ms で指定された時間の刻み幅ごとに呼び出される。
   * ここでは経過時間を加算して次のワールドを構成している。
   **/
  override def tick(): Timer1 = { Timer1(t + 1, tick_ms) }
}

/**
  * 初期ワールドの構成。ここから世界が始まる。
  * 最初のふたつの引数は表示するウィンドウの横と縦の長さをピクセル数で指定したもの。
  * 三番目の引数は画面更新の頻度を秒単位で指定したもの。あるいは10ミリ秒ごとにtick関数が呼ばれる。
  **/
@main def run = World.bigbang2d(new Timer1(0, 10), "Timer 1", 300, 100)
