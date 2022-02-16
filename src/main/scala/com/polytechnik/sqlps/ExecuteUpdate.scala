package com.polytechnik.sqlps;

/**
  *  Execute an UPDATE/INSERT type of statement.
  *  Seldom used, more typical -- use [[ReadObjs]] with SQL containing 
  *  RETURNING *.
  */
class ExecuteUpdate(
  private val toSearch: SQLst,
  private val flagClosePreparedStatement:Boolean=true,
  private val flagConnectionCommit:Boolean=false){

  def apply(connection:java.sql.Connection):Int = {
    val st=connection.prepareStatement(toSearch.getSQL())
    // initialize all JDBC arguments
    toSearch.setAllValues(st)
    val result=st.executeUpdate();
    if(flagClosePreparedStatement){
      st.close()
    }
    if(flagConnectionCommit){
      connection.commit()
    }
    result;
  }
}

object ExecuteUpdate{
   def apply(
     toSearch: SQLst,
     flagClosePreparedStatement:Boolean=true)=new ExecuteUpdate(toSearch,flagClosePreparedStatement)
}
