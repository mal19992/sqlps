package com.polytechnik.sqlps.csql

import com.polytechnik.sqlps.SQLargs._
import com.polytechnik.sqlps.SQLst

/** Manually selected list of inodes.
  */
trait ManuallySelected extends VirtualCohort {

  protected def getList:scala.collection.Seq[Long]

  override def getSelectSQL():SQLst={
    val list=getList

    val l=list.size+100
    val tname=sql""" ${vtTableName}_tt (${colObjInodeName},${colWeightName})"""

    val sel=if(list.size>0){
      sql""" SELECT * FROM VALUES
      (${SQLst.mergeWithSeparator(list.distinct.zipWithIndex.map(x=>sql"(${aLong(x._1)},${aInt(l-x._2)})"),",")})
       AS ${tname}"""
    } else {
      sql""" SELECT * FROM (VALUES (0::bigint,0::integer)) AS ${tname} WHERE false""" 
    }
    sql"""
(/* BEGIN ${vtTableName} */
${sel}
/* END ${vtTableName} */)
"""
  }
}
