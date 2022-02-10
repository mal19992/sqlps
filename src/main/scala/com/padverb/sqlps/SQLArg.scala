package com.padverb.sqlps;

/** An interface to a single prepared statement argument
*/
trait SQLArg {
  /** SQL to insert, for a single argument typical value is ?.
    *  For multiple arguments the number of ? is equal to the number of arguments.
    */
  def getSQL():String
  /** Initialize prepared statement.
    *  @param s Prepared statement to initialize.
    *  @param n The number (SQL count, base 1) of first argument to initialize.
    *  @return The number or arguments set.
    */
  def setArg(s:java.sql.PreparedStatement,n:Int):Int
}
