**Simple in-memory transactional key-value store.**

* Supports `get`, `set`, `delete`, `count`, `begin`, `rollback`, `commit` operations.
* Supports nested transactions.
* Commit/rollback operations apply changes from the last transaction.
* Parallel/concurrent transactions are not supported.
* Not thread-safe!

Has 2 implementations:
1. [TkvsSingleMap.kt](src/main/kotlin/TkvsSingleMap.kt)<br>
   Implementation with single map, change logs, and value counters cache for *O(1)* computational complexity of count operation.
2. [TkvsStackOfMaps.kt](src/main/kotlin/TkvsStackOfMaps.kt)<br>
   Implementation based on stack of maps.

Unit tests: [TransactionalKeyValueStoreTestBase.kt](src/test/kotlin/TransactionalKeyValueStoreTestBase.kt)

A built jar is available in [bin/tkvs.jar](../../raw/main/bin/tkvs.jar).<br>
Run it with `java -jar bin/tkvs.jar` for an interactive console (uses "single map" implementation).<br>
In the console, type `h` or `help` to see available commands.
