package com.polytechnik.sqlps;

class SQLst(private val parts:scala.collection.immutable.Seq[String],private val args:scala.collection.Seq[Any]){

  if(parts.size!=args.size+1) throw new IllegalArgumentException("Size mismatch");

  private lazy val ps=SQLst.sqlPSInterpolator(this)
  /** Init all values of a prepared statement.
    *  @return The total number of values set.
    */
  def setAllValues(s: java.sql.PreparedStatement): Int ={
    // SQL prepared statements args start with 1!!!
    ps.functs.foldLeft(1)((n, x) => n + x(s, n)) - 1
  }

  def getSQL():String=ps.sql

  def appendString(s:String):SQLst=SQLst.merge(this,SQLst.fromString(s))
  def prependString(s:String):SQLst=SQLst.merge(SQLst.fromString(s),this)

  /** An alias for [[SQLst.appendString]].
    */
  def +(s:String)=appendString(s)

  /** Merges this object with another one.
    */
  def +(s:SQLst)=SQLst.merge(this,s)

}

object  SQLst {
  def empty = new SQLst(List(""), Seq.empty)

  private class LinearizedTree(val functs:scala.collection.Seq[(java.sql.PreparedStatement,Int)=>Int],val sql:String)

  private val process: String => String=StringContext.processEscapes
  private def sqlPSInterpolator(e:SQLst): LinearizedTree = {
    StringContext.checkLengths(e.args, e.parts)
    // already in constructor
    //if(e.parts.size!=e.args.size+1) throw new IllegalArgumentException("Size mismatch");

    val bldr = new java.lang.StringBuilder(process(e.parts.head))
    val fs = new scala.collection.mutable.ArrayBuffer[(java.sql.PreparedStatement,Int)=>Int]()
    for((pstr,arg) <- e.parts.tail.zip(e.args)){
      arg match {
        case a:SQLst => {
          val l=sqlPSInterpolator(a)
          fs.appendAll(l.functs)
          bldr.append(l.sql)
        }
        case a:SQLArg =>{
          fs.append(a.setArg)
          bldr.append(a.getSQL())
        }
        case a => {
          bldr.append(a)
        }
      }
      bldr.append(process(pstr))
    }
    new LinearizedTree(fs,bldr.toString)
  }


  def merge(s:SQLst*):SQLst={
    new SQLst(List.fill(s.size+1)(""),s)
  }
  def fromString(s:String):SQLst=new SQLst(List.fill(2)(""),List(s))  // pass as value to avoid unescape

  def mergeWithSeparator(s:scala.collection.Seq[SQLst],separator:String=""):SQLst=mergeWithSeparatorSQL(s,fromString(separator))
  def mergeWithSeparatorSQL(s:scala.collection.Seq[SQLst],separator:SQLst=fromString("")):SQLst={
    val f = new scala.collection.mutable.ArrayBuffer[SQLst]()
    for((x,i) <- s.zipWithIndex){
      if(i>0) f.append(separator)
      f.append(x)
    }
    merge(f.toList:_*)
  }
  

}

