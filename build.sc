import mill._
import $ivy.`com.lihaoyi::mill-contrib-playlib:`,  mill.playlib._

object hotelreservationapi extends PlayModule with SingleModule {

  def scalaVersion = "2.13.16"
  def playVersion = "2.9.6"
  def twirlVersion = "1.6.2"

  object test extends PlayTests
}
