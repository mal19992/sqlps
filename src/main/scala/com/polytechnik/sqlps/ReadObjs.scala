package com.polytechnik.sqlps;

/**
  * Read several objects.
  * @param toSearch SQL to execure.
  * @param extractT Data extractor from ResultSet.
  * @param flagClosePreparedStatement Whether to close ResultSet on exit.
  */
class ReadObjs[T](
  private val toSearch: SQLst,
  private val extractT:java.sql.ResultSet=>T,
  private val flagClosePreparedStatement:Boolean=true,
  private val flagConnectionCommit:Boolean=false) {

  /** Returns the data as a sequence of objects.
  * @return The data.
  */
  def apply(connection:java.sql.Connection):scala.collection.Seq[T] = {
    val result=new scala.collection.mutable.ArrayBuffer[T]();
    val st=connection.prepareStatement(toSearch.getSQL())
    // initialize all JDBC arguments
    toSearch.setAllValues(st)
    //if(com.padverb.C.diagLevel>10) System.err.println("ReadObjs: st="+st);
    val res = st.executeQuery();
    while (res.next()) {
      result += extractT(res)
    }
    if(flagClosePreparedStatement){
      st.close()
    }
    if(flagConnectionCommit){
      connection.commit()
    }
    result;
  }

}

object ReadObjs{
  def apply[T](
    toSearch: SQLst,
    extractT:java.sql.ResultSet=>T,
    flagClosePreparedStatement:Boolean=true,
    flagConnectionCommit:Boolean=false)=new ReadObjs[T](
      toSearch,
      extractT=extractT,
      flagClosePreparedStatement=flagClosePreparedStatement,
      flagConnectionCommit=flagConnectionCommit
  )
}
