package com.rtjvm.scala.oop.commands

import com.rtjvm.scala.oop.files.Directory
import com.rtjvm.scala.oop.filesystem.State

trait Command extends (State => State)

object Command {

  def findAbsolutePath(state: State, name: String): String = {
    // 1. get working dir
    val wd = state.wd

    // 2. get absolute path
    if (name.startsWith(Directory.SEPARATOR)) name
    else s"${wd.path}${if (!wd.isRoot) Directory.SEPARATOR else ""}$name"
  }

  val MKDIR = "mkdir"
  val LS = "ls"
  val PWD = "pwd"
  val TOUCH = "touch"
  val CD = "cd"
  val RM = "rm"
  val ECHO = "echo"
  val CAT = "cat"

  def emptyCommand: Command = (state: State) => state

  def incompleteCommand(name: String): Command =
    (state: State) => state.setMessage(s"$name: incomplete command!")

  def from(input: String): Command = {
    val tokens: Array[String] = input.split(' ').filter(_.trim.nonEmpty)

    if (tokens.isEmpty) emptyCommand
    else tokens(0).toLowerCase() match {
      case MKDIR =>
        if (tokens.length < 2) incompleteCommand(MKDIR)
        else new Mkdir(tokens(1))
      case LS => Ls
      case PWD => Pwd
      case TOUCH =>
        if (tokens.length < 2) incompleteCommand(TOUCH)
        else Touch(tokens(1))
      case CD =>
        if (tokens.length < 2) incompleteCommand(CD)
        else Cd(tokens(1))
      case RM =>
        if (tokens.length < 2) incompleteCommand(RM)
        else Rm(tokens(1))
      case ECHO =>
        if (tokens.length < 2) incompleteCommand(ECHO)
        else Echo(tokens.tail)
      case CAT =>
        if (tokens.length < 2) incompleteCommand(CAT)
        else Cat(tokens(1))
      case _ => UnknownCommand
    }
  }
}

