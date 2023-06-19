package com.polytechnik.sqlps.csql

import com.polytechnik.sqlps.SQLargs._
import com.polytechnik.sqlps.SQLst

/** An empty cohort.
  */
trait EmptyCohort extends VirtualCohort {


  override def getSelectSQL():SQLst={
    sql"""
(/* BEGIN ${vtTableName} */
 SELECT * FROM (VALUES (0::bigint,0::integer)) AS
  ${vtTableName}_tte (${colObjInodeName},${colWeightName})
 WHERE false
/* END ${vtTableName} */)
"""
  }

}
