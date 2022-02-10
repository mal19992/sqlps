package com.padverb.sqlps;

/**
  *  Execute an UPDATE/INSERT type of statement.
  *  Seldom used, more typical -- use [[ReadObjs]] with SQL containing 
  *  RETURNING *.
  */
class ExecuteUpdate(
  private val toSearch: SQLst,
  private val flagClosePreparedStatement:Boolean=true){

  def apply(connection:java.sql.Connection):Int = {
    val st=connection.prepareStatement(toSearch.getSQL())
    // initialize all JDBC arguments
    toSearch.setAllValues(st)
    val result=st.executeUpdate();
    if(flagClosePreparedStatement){
      st.close()
    }
    result;
  }
}

object ExecuteUpdate{
   def apply(
     toSearch: SQLst,
     flagClosePreparedStatement:Boolean=true)=new ExecuteUpdate(toSearch,flagClosePreparedStatement)
}
