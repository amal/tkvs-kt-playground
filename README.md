Simple in-memory transactional key-value store.

* Supports `get`, `set`, `delete`, `count`, `begin`, `rollback`, `commit` operations.
* Supports nested transactions.
* Commit/rollback operations apply changes from the last transaction.
* Parallel/concurrent transactions are not supported.
* Not thread-safe!

Main implementation calss: [TransactionalKeyValueStore.kt](src/main/kotlin/TransactionalKeyValueStore.kt)

A built jar is available in [bin/tkvs.jar](../../raw/main/bin/tkvs.jar).<br>
Run it with `java -jar bin/tkvs.jar` for an interactive console.<br>
In the console, type `h` or `help` to see available commands.
