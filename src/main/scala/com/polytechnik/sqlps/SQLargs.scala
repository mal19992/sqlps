package com.polytechnik.sqlps;

/** Commonky used prepared statement argument types. */
object SQLargs {
  private class SimpleOneArg[T](
    private val v:T,
    private val sql:String,
    private val setValue:(java.sql.PreparedStatement,Int,T)=>Unit) extends SQLArg {
    def getSQL():String=sql
    def setArg(s:java.sql.PreparedStatement,n:Int):Int={setValue(s,n,v);1/* Single argument */}
  }

  /** An implictit class to initialize sql"" ... """ type of strings.
    */
  implicit class StringContextForSQLPreparedStatement(val sc: StringContext) extends AnyVal {
  def sql(args: Any*): SQLst = {
    //System.err.println("parts="+sc.parts+" args="+args)
    new SQLst(sc.parts,args)
  }
}



  def aLong(a:Long,sql:String="?"):SQLArg=new SimpleOneArg[Long](v=a,sql=sql,_.setLong(_,_))
  def aInt(a:Int,sql:String="?"):SQLArg=new SimpleOneArg[Int](v=a,sql=sql,_.setInt(_,_))
  def aDouble(a:Double,sql:String="?"):SQLArg=new SimpleOneArg[Double](v=a,sql=sql,_.setDouble(_,_))
  def aBoolean(a:Boolean,sql:String="?"):SQLArg=new SimpleOneArg[Boolean](v=a,sql=sql,_.setBoolean(_,_))
  def aString(a:String,sql:String="?"):SQLArg=new SimpleOneArg[String](v=a,sql=sql,_.setString(_,_))
  def aObject(a:Object,sql:String="?"):SQLArg=new SimpleOneArg[Object](v=a,sql=sql,_.setObject(_,_))
  def aURL(a:java.net.URL,sql:String="?"):SQLArg=new SimpleOneArg[java.net.URL](v=a,sql=sql,_.setURL(_,_))
  def aUUID(a:java.util.UUID,sql:String="?"):SQLArg=new SimpleOneArg[java.util.UUID](v=a,sql=sql,_.setObject(_,_))

  /** Is this a good idea? 
    */
  /* BAD IDEA. null and etc.
  def a(a: Any, sql: String = "?"): SQLArg = {
    a match {
      case a: Long    => aLong(a, sql)
      case a: Int     => aInt(a, sql)
      case a: Double  => aDouble(a, sql)
      case a: Boolean => aBoolean(a, sql)
      case a: String  => aString(a, sql)
      case a: java.net.URL => aURL(a, sql)
      case a: java.util.UUID => aUUID(a, sql)
      case a => throw new RuntimeException("call setObject directly.") //Object  => aObject(a, sql)
    }
  }
   */

  def aArrayOfLong(a:scala.collection.Seq[Long],sql:String="?::bigint[]"):SQLArg=new SimpleOneArg[scala.collection.Seq[Long]](v=a,sql=sql,(s,n,v)=>{
    s.setArray(n,s.getConnection().createArrayOf("bigint",scala.jdk.javaapi.CollectionConverters.asJava(a).toArray()))
  })


  def aArrayOfInt(a:scala.collection.Seq[Int],sql:String="?::integer[]"):SQLArg=new SimpleOneArg[scala.collection.Seq[Int]](v=a,sql=sql,(s,n,v)=>{
    s.setArray(n,s.getConnection().createArrayOf("integer",scala.jdk.javaapi.CollectionConverters.asJava(a).toArray()))
  })

  def aArrayOfDouble(a:scala.collection.Seq[Double],sql:String="?::float8[]"):SQLArg=new SimpleOneArg[scala.collection.Seq[Double]](v=a,sql=sql,(s,n,v)=>{
    s.setArray(n,s.getConnection().createArrayOf("float8",scala.jdk.javaapi.CollectionConverters.asJava(a).toArray()))
  })

  def aArrayOfFloat(a:scala.collection.Seq[Float],sql:String="?::float4[]"):SQLArg=new SimpleOneArg[scala.collection.Seq[Float]](v=a,sql=sql,(s,n,v)=>{
    s.setArray(n,s.getConnection().createArrayOf("float4",scala.jdk.javaapi.CollectionConverters.asJava(a).toArray()))
   })

  def aArrayOfString(a:scala.collection.Seq[String],sql:String="?"):SQLArg=new SimpleOneArg[scala.collection.Seq[String]](v=a,sql=sql,(s,n,v)=>{
    s.setArray(n,s.getConnection().createArrayOf("text",scala.jdk.javaapi.CollectionConverters.asJava(a).toArray()))
  })

  def aArrayOfObject(sqlObjType:String,a:scala.collection.Seq[Object],sql:String="?"):SQLArg=new SimpleOneArg[scala.collection.Seq[Object]](v=a,sql=sql,(s,n,v)=>{
    s.setArray(n,s.getConnection().createArrayOf(sqlObjType,scala.jdk.javaapi.CollectionConverters.asJava(a).toArray()))
  })

  /** A two--argument of Long type example.
    *  @param a First value
    *  @param a2 Second value
    *  @param sql a portion of SQL query (must have two ? as two arguments are set, e.g. (?,?).
    */
  def aTwoLongExample(a:Long,a2:Long,sql:String):SQLArg=new SQLArg(){
    def getSQL():String=sql
    def setArg(s:java.sql.PreparedStatement,n:Int):Int={
      s.setLong(n,a)
      s.setLong(n+1,a2)
      2/* Two argument are set */
    }
  }

}
    
