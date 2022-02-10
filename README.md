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

To simplify the syntax a string interpolation with `sql"""...."""` is implementd.
An example:
