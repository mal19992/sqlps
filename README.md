# sqlps
Scala JDBC prepared statement library


The problem of integration between a programming language and SQL is a very common task.
In JVM world it is typically approached using some kind of wrapper above JDBC.
There are approaches such as
[anorm](http://playframework.github.io/anorm/),
[slick](https://scala-slick.org/doc/3.2.1/sql.html)
and others that integrate scala and SQL rather deeply.

However, in practice direct mapping of scala structures
to SQL using some kind of DSL
is not always a good idea as generated SQL often has it's own
structural components (scala structures generating a piece of SQL)
that are manually combined to an SQL request, it is quite problematic
to convert automatically using some kind of DSL.

For reading the data a wrapper of
[java.sql.RedultSet](https://docs.oracle.com/en/java/javase/17/docs/api/java.sql/java/sql/ResultSet.html)
is typically easy to write.
For creating SQL it is just a string concatenation
if programming language values are directly incorporated to request,
or some meta-language (e.g. the symbol "?" in JDBC) that is later
intepreted as the place to include the value of a prepared statement.
Currently a "direct inclusion" of values to SQL request is considered insecure and
[java.sql.PreparedStatement](https://docs.oracle.com/en/java/javase/17/docs/api/java.sql/java/sql/PreparedStatement.html)
is the way to go. Proposed library addresses the problem in a type--safe way. It is small (below 200 lines) and fast.
In contrast with other wrappers
[TINKOFF](https://habr.com/ru/company/tinkoff/blog/193396/)
it can combine both non--sql (e.g. strings) and sql, the distinction
is made by the object type.

The concept is to have two types:
* [SQLArg](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/SQLArg.html) for a single prepared statement argument
* [SQLst](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/SQLst.html) as a container for SQL query or it's portion.

The later has two important methods
[getSQL](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/SQLst.html#getSQL():String) to obtain SQL request as String and
[setAllValues](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/SQLst.html#setAllValues(s:java.sql.PreparedStatement):Int) to initialize a prepared statement with the values.

To simplify the syntax a string interpolation with `sql"""...."""` is implemented
to create an object of
[SQLst](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/SQLst.html)
type
An example:
```
import com.padverb.sqlps.arg._ // implicit sql"...", aLong, aString, etc...

val q=sql"""SELECT * FROM tableX WHERE y=${aLong(33)}"""
// created q:SQLst ; q.getSQL()="SELECT * FROM tableX WHERE y=?"
```
in the object [arg](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/arg$.html)
an implisit string interpolation method `sql"""...."""` is set
along with definition of methods
[aLong(Long,String):SQLArg](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/arg$.html#aLong(Long,String):SQLArg)
[aString(String,String):SQLArg](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/arg$.html#aString(String,String):SQLArg)
[aInt(Int,String):SQLArg](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/arg$.html#aInt(Int,String):SQLArg), and others for other types. One can implement his own methods as necessary.

These methods return an instance of
[SQLArg](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/SQLArg.html)
class that is used for prepared statement initialization.
Regular string interpolation can be used as well
```
val tableName="tableX"
val q=sql"""SELECT * FROM ${tableName} WHERE y=${aLong(33)}"""
```
The interpolator distinguishes prepared statement and the values
to be directly interpolated by their type. Two types
are treated specialy by the `sql` interpolator:
[SQLArg](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/SQLArg.html)
and
[SQLst](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/SQLst.html).

For [SQLArg](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/SQLArg.html)
types the value to be inserted to SQL is the one returned by 
[getSQL()](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/SQLArg.html#getSQL():String) method, by default it is `?`, but it can be changed to anything, e.g.:
```
val q=sql"""SELECT * FROM tableX WHERE ${aLong(33,"y=?")}""" // q:SQLst
```
This `q.getSQL()` also produces
`SELECT * FROM tableX WHERE y=?`,
same as in the example above.

The second method of
[SQLst](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/SQLst.html)
is the
[setAllValues](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/SQLst.html#setAllValues(s:java.sql.PreparedStatement):Int)
that performs SQL initialization of the prepared statemenets, e.g.:
```
import com.padverb.sqlps.arg._ // implicit sql"...", aLong, aString, etc...

val q=sql"""SELECT * FROM tableX WHERE y=${aLong(33)} and z=${aString("abc")}"""
// created q:SQLst, getSQL() is: SELECT * FROM tableX WHERE y=? and z=?

val st=some_jdbc_connection.prepareStatement(q.getSQL())
q.setAllValues(st) // will issue st.setLong(1,33), st.setString(2,"abc")
```

This way a [SQLst](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/SQLst.html) object carry information abouth both: SQL statement and prepared statement arguments initialization.
There are two convenience wrappers:
* [ReadObjs](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/ReadObjs$.html) Read multiple objects
* [ReadObjOpt](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/ReadObjOpt$.html) Read a single object

For example
```
import com.padverb.sqlps._
import com.padverb.sqlps.arg._ // implicit sql"...", aLong, aString, etc...

val q=sql"""SELECT * FROM tableX WHERE y=${aLong(33)} and z=${aString("abc")}"""
// created q:SQLst, getSQL() is: SELECT * FROM tableX WHERE y=? and z=?
val result=ReadObjs(q,rs=>(rs.getLong("y"),rs.getString("z")))(some_jdbc_connection)
```
will return the result determined by the second argument type (a function extracting
the data from `java.sql.ResultSet`.
This extractor function (e.g. `extractTypeT:java.sql.ResultSet=>T` and `extractTypeR:java.sql.ResultSet=>R`)
is typically stored somewhere and SQL request my look like:
```
val dataTypeT=ReadObjs(
		sql"""SELECT * FROM tableX WHERE y=${aLong(33)} and z=${aString("abc")}"""
    		extractTypeT)(some_jdbc_connection)
// the scala.collection.Seq[T] is returned

val dataTypeR=ReadObjs(
		sql"""SELECT * FROM tableX WHERE y=${aLong(33)} and z=${aString("abc")}"""
    		extractTypeR)(some_jdbc_connection)
// the Option[R] is returned
```

This is a typical SQL interpolation functionality, used in most java/scala frameworks. 
The difference with this library is that SQL-pieces (of
[SQLst](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/SQLst.html)
type) can itself be interpolated by the `sql""" ... """` interpolator,
e.g:
```
val q1=sql"""SELECT z FROM tableX WHERE z=${aString("abc")}"""
val q=sql""" SELECT * FROM tableX WHERE x=${aLong(33)} AND z IN (${q1})"""
// created q:SQLst q.getSQL()=" SELECT * FROM tableX WHERE x=? AND z IN (SELECT y FROM tableX WHERE z=?)"
```
when issued `q.setAllValues(st)` the SQL prepared statement will be properly initialized regardless the
order/depth of used "sql pieces" of [SQLst](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/SQLst.html) type. Inside the interpolator there is a recursive tree walk, this makes it possible.

Hence the proposed library alows a seamless integration
of scala language variables and SQL prepared statement variables.
The goal was achieved by introduction of two types
[SQLArg](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/SQLArg.html)
and
[SQLst](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/SQLst.html)
and treating them specially during `sql"..."` interpolation.

In some cases
it is convenient to create
[SQLst](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/SQLst.html)
directly, without `sql"..."` interpolation, e.g. let we have an array of `data:(Long,Int)` tuples,
then
```
val data=List((101L,1),(102L,2),(103L,3))

val q=sql"INSERT INTO VALUES "+SQLst.mergeWithSeparator(
s=for((x,i)<-data) yield sql"""(x=${aLong(x)},i=${aInt(i)})""",
separator=",")+ sql" RETURNING * "
// will create q.getSQL()="INSERT INTO VALUES (x=?,i=?),(x=?,i=?),(x=?,i=?) RETURNING * "
```
and the values will be properly binded by `q.selAllValues(st)` or using 
[ReadObjs](https://mal19992.github.io/sqlps/docs/api/com/padverb/sqlps/ReadObjs$.html)
wrapper
```
val result=ReadObjs(q,rs=>(rs.getLong("x"),rs.getInt("i")))(some_jdbc_connection)
```
