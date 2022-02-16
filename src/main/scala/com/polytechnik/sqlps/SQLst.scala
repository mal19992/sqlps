package com.polytechnik.sqlps;

class SQLst(private val parts:scala.collection.immutable.Seq[String],private val args:scala.collection.Seq[Any]){

  private lazy val ps=SQLst.sqlPSInterpolator(this)
  /** Init all values of a prepared statement.
    *  @return The total number of values set.
    */
  def setAllValues(s:java.sql.PreparedStatement):Int={
    var n=1 // prepared statements args start with 1
    for(x <- ps.functs){
      n=n+x(s,n)
    }
    n-1
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
  private class LinearizedTree(val functs:scala.collection.Seq[(java.sql.PreparedStatement,Int)=>Int],val sql:String)

  private val process: String => String=StringContext.processEscapes
  private def sqlPSInterpolator(e:SQLst): LinearizedTree = {
    StringContext.checkLengths(e.args, e.parts)
    val pi = e.parts.iterator
    val ai = e.args.iterator
    val bldr = new java.lang.StringBuilder(process(pi.next()))
    val fs = new scala.collection.mutable.ArrayBuffer[(java.sql.PreparedStatement,Int)=>Int]()
    while (ai.hasNext) {
      ai.next() match {
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
      bldr.append(process(pi.next()))
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

