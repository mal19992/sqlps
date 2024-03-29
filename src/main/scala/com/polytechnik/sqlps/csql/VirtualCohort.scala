package com.polytechnik.sqlps.csql

import com.polytechnik.sqlps.SQLargs._
import com.polytechnik.sqlps.SQLst

/** A "virtual" cohort. A SELECT of special form.
  */

trait VirtualCohort {
  def vtTableName :String
  def colObjInodeName :String
  def colWeightName :String


  /** A method to generate SQL SELECT used in "category" joins.
    *
    * Typical usage: testing whether "colObjInodeName" belongs to the
    * cohort. The "colWeightName" is the object's rank in
    * this cohort.
    */
  def getSelectSQL(): SQLst


  // below are trivial utility methods
  def inodeName=vtTableName+"."+colObjInodeName
  def weightName=vtTableName+"."+colWeightName
  /** Order by the weightName.
    */
  def orderByFields():SQLst=SQLst.fromString(weightName)+" DESC"

  /** A helper method to append table name.
    */
  def getJoinSQL(): SQLst = getSelectSQL() +s""" AS ${vtTableName}"""

  /** A helper method to prepend table name.
    */
  def getWithSQL(): SQLst = SQLst.fromString(vtTableName)+" AS "+getSelectSQL()

}
