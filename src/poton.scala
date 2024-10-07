package prg1.lx02.poton

import scala.math.{max, min}

import prg1.support.world._
import prg1.graphics.color._
import java.awt.Color => JColor
import java.awt.event.KeyEvent => JKeyEvent


case class Poton(centerX: Int, centerY: Int, tick_ms: Int) extends World(tick_ms) {
  import Poton._

  /*
   以下の一連の定義が val でないのには World.canvas の初期化でサイズ (0, 0) なキャンバスを作成していることに起因するややこしい事情がある。問題の根幹は最初の world が作成される時点では、空の canvas が構成されていること。
   def にすることで、minX, maxX, ... が参照されたタイミングにおける canvas.size が利用されるためバグが避けられている。もっときれいな解決（「初期キャンバスを大きくする」ではなく本質的な）が求められる。
   */
  def s = World.canvas.size
  def minX  = BallRadius + 10             // X座標の最小値（これ以上左に寄ると左の壁に衝突）
  def maxX  = s.width  - BallRadius - 10  // X座標の最大値（これ以上、右に寄ると右の壁に衝突）
  def maxY  = s.height - BallRadius * 2   // Y座標の最大値（これ以上、落ちると画面から消える）
  def holeX = s.width / 2                 // 穴の中心のX座標
  if (World.debug) println((s, centerX, centerY))

  override def draw() = {
    drawRect(Pos(0, 0), s.width, s.height, White)    // 画面消去
    drawRect(Pos(10, maxY - 10), holeX - 10, 20, Green)        // 左方の草原の描画
    drawRect(                                                  // 右方の草原の描画
      Pos(holeX + (BallRadius * 2 + 10), maxY - 10),     // 矩形の位置: Pos(x, y)
      s.width - (holeX + (BallRadius * 2 + 10)),    // 矩形の幅
      20,                                                // 矩形の高さ
      Green                                              // 塗り潰し色
    )
    drawDisk(Pos(centerX, centerY), BallRadius, Blue)          // ボール
  }

  override def tick(): World = {
    Poton(centerX, min(centerY + 5, maxY), tick_ms)
  }

  override def keyTyped(c: Char): World = {
    c match {
      case ' ' => Poton(centerX, maxY, tick_ms)
      case 'h' => Poton(max(minX, centerX - 5), centerY, tick_ms)
      case 'l' => Poton(min(centerX + 5, maxX), centerY, tick_ms)
      case _             => Poton(centerX, centerY, tick_ms)
    }
  }
}

object Poton {
  val BallRadius  =  50

  val White = Color(JColor.white)
  val Blue  = Color(JColor.blue)
  val Green = Color(JColor.green)
}

// Run this app from sbt: [project lxz; runMain poton.A]
@main def main = {
  import Poton._
  val world = Poton(400, BallRadius * 2, 100)
  World.bigbang2d(world, "Poton", 800, 600)
}