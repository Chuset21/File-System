package com.rtjvm.scala.oop.files

abstract class DirEntry(val parentPath: String, val name: String) {

  def path: String =
    s"$parentPath${if (!Directory.ROOT_PATH.equals(parentPath)) Directory.SEPARATOR else ""}$name"

  def asDirectory: Directory

  def asFile: File

  def isDirectory: Boolean

  def isFile: Boolean

  def getType: String
}
