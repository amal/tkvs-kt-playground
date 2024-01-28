package kt.tkvs

interface TransactionalKeyValueStore {

    /**
     * The current transaction level.
     *
     * @return `0` if there are no ongoing transactions.
     */
    val transactionLevel: Int


    /**
     * Return the current value for the [key].
     *
     * @return `<Key '' not set>` if there was no such key.
     */
    operator fun get(key: String): String?

    /**
     * Return the number of keys that have the given [value].
     * Computational complexity is `O(1)` as uses a cached counters.
     */
    fun count(value: String): Int

    /**
     * Store the [value] for [key].
     *
     * @return the previous value for the [key] or `null` if there was no such key.
     */
    operator fun set(key: String, value: String): String?

    /**
     * Remove the entry for [key].
     *
     * @return the previous value for the [key] or `null` if there was no such key.
     */
    fun delete(key: String): String?


    /**
     * Start a new transaction.
     */
    fun begin()

    /**
     * Complete the current transaction.
     *
     * @throws IllegalArgumentException if there is no transaction to commit.
     */
    fun commit()

    /**
     * Revert to state prior to [begin] call.
     *
     * @throws IllegalArgumentException if there is no transaction to rollback.
     */
    fun rollback()


    companion object {
        const val NO_TRANSACTION = "no transaction"
    }
}
