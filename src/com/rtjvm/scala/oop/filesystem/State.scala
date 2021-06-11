package com.rtjvm.scala.oop.filesystem

import com.rtjvm.scala.oop.files.Directory

final case class State(root: Directory, wd: Directory, output: String) {

  def show(): Unit = {
    println(output)
    print(State.SHELL_TOKEN)
  }

  def setMessage(message: String): State =
    State(root, wd, message)
}

object State {
  val SHELL_TOKEN = "$ "

  def apply(root: Directory, wd: Directory, output: String = ""): State =
    new State(root, wd, output)
}
