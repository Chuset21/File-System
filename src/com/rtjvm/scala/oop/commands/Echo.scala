package com.rtjvm.scala.oop.commands

import com.rtjvm.scala.oop.files.{Directory, File}
import com.rtjvm.scala.oop.filesystem.State

import scala.annotation.tailrec

final case class Echo(args: Array[String]) extends Command {

  override def apply(state: State): State = {
    /*
      if no args, state
      else if just one arg, print to console
      else if multiple args
      {
        operator = next to last argument
        if >
          echo to a file (may create a file if not there)
        if >>
          append to a file
        else
          just echo everything to console
      }
     */
    if (args.length == 1) state.setMessage(args(0))
    else {
      val topIndex = args.length - 2
      val operator = args(topIndex)
      val fileName = args(args.length - 1)
      val contents = createContent(args, topIndex)

      if (">>".equals(operator)) doEcho(state, contents, fileName, append = true)
      else if (">".equals(operator)) doEcho(state, contents, fileName, append = false)
      else state.setMessage(createContent(args, args.length).trim)
    }
  }


  // topIndex NON-INCLUSIVE!
  def createContent(args: Array[String], topIndex: Int): String = {
    @tailrec
    def createContentHelper(currentIndex: Int, accumulator: String): String =
      if (currentIndex >= topIndex) accumulator
      else createContentHelper(currentIndex + 1, s"$accumulator ${args(currentIndex)}")

    createContentHelper(0, "")
  }

  def getRootAfterEcho(currentDirectory: Directory, path: List[String], contents: String,
                       append: Boolean): Directory = {
    if (path.isEmpty) currentDirectory
    else if (path.tail.isEmpty) {
      val dirEntry = currentDirectory.findEntry(path.head)
      if (dirEntry == null) {
        currentDirectory.addEntry(new File(currentDirectory.path, path.head, contents))
      } else if (dirEntry.isDirectory) currentDirectory
      else if (append) currentDirectory.replaceEntry(path.head, dirEntry.asFile.appendContents(contents))
      else currentDirectory.replaceEntry(path.head, dirEntry.asFile.setContents(contents))
    } else {
      val nextDirectory = currentDirectory.findEntry(path.head).asDirectory
      val newNextDirectory = getRootAfterEcho(nextDirectory, path.tail, contents, append)

      if (newNextDirectory == nextDirectory) currentDirectory
      else currentDirectory.replaceEntry(path.head, newNextDirectory)
    }
  }

  def doEcho(state: State, contents: String, fileName: String, append: Boolean): State = {
    if (fileName.contains(Directory.SEPARATOR)) state.setMessage("Echo: filename must not contain separators")
    else {
      val newRoot = getRootAfterEcho(state.root, state.wd.getAllFoldersInPath :+ fileName, contents, append)
      if (newRoot == state.root) state.setMessage(s"$fileName: no such file")
      else State(newRoot, newRoot.findDescendant(state.wd.getAllFoldersInPath))
    }
  }
}
