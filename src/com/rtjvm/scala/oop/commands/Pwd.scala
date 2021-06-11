package com.rtjvm.scala.oop.commands

import com.rtjvm.scala.oop.filesystem.State

object Pwd extends Command {
  override def apply(state: State): State =
    state.setMessage(state.wd.path)
}
