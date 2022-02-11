package com.padverb.sqlps;

/**
  * Read an object (if any). If there is more than one object in ResultSet -- throw an exception.
  * @param toSearch SQL to execure.
  * @param extractT Data extractor from ResultSet.
  * @param flagClosePreparedStatement Whether to close ResultSet on exit.
  */
class ReadObjOpt[T](
  private val toSearch: SQLst,
  private val extractT:java.sql.ResultSet=>T,
  private val flagClosePreparedStatement:Boolean=true,
  private val flagConnectionCommit:Boolean=false) {

  /** Returns the data as a sequence of objects.
    * @return The data.
    */
  def apply(connection:java.sql.Connection):Option[T] = {
    var result:Option[T]=None
    val st=connection.prepareStatement(toSearch.getSQL())
    // initialize all JDBC arguments
    toSearch.setAllValues(st)
    val res = st.executeQuery();
    var n=0
    while (res.next()) {
      // assume there is a single object to read
      n=n+1
      if(n>1) throw new IllegalStateException("There is more than one object read from ResultSet")
      result = Some(extractT(res))
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

object ReadObjOpt{
  def apply[T](
    toSearch: SQLst,
    extractT:java.sql.ResultSet=>T,
    flagClosePreparedStatement:Boolean=true)=new ReadObjOpt[T](
    toSearch,extractT,flagClosePreparedStatement
  )
}
