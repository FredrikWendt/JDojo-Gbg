The case:
You're in a development team that wrote some software which was to 
be customized by some other team. That didn't go very well and ended
up with the customization being raw SQL statements and JDBC code.

As time has passed, you're starting to move away from SQL, but still
need to provide a working API for customers that can't leave the SQL
server they're not deeply involved with/in

So, it's time to write an API that's storage technique agnostic. One
that you can write a SQL implementation for, one with plain text file
storage, and a Redis ("noSQL") implementation.

In other words:

there's "legacy" code and tests for the old poor customization. It's 
now your job/task/exercise to:

a) write a new API that can deliver the same objects/information,
   adjust the test suite to the new API
b) write an implementation that works with the "old" SQL tables.
c) write a second implementation of the same API, that uses plain
   flat text files as storage
d) write a third implementation of the same API, that uses a noSQL
   solution

Good luck!
