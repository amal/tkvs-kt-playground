@file:Suppress("TooManyFunctions")

package kt.tkvs

import java.util.LinkedList

/**
 * An in-memory transactional key-value store.
 *
 * Implementation with **single map + change logs + value counters cache**.
 *
 * * Supports nested transactions.
 * * Commit/rollback operations apply changes from the last transaction.
 * * Parallel/concurrent transactions are NOT supported.
 *
 * **WARNING**: The class is not thread-safe!
 */
class TkvsSingleMap : TransactionalKeyValueStore {

    /**
     * The main key-value store.
     */
    private val mainStore = mutableMapOf<String, String>()

    /**
     * Value counters to support effective [Command.Count] operations.
     */
    private val countersStore = mutableMapOf<String, Int>()

    /**
     * A list of ongoing transaction change logs. Each is a list of changes.
     * Multiple values mean nested transactions.
     * The last one is the current.
     */
    private val transactions = LinkedList<MutableList<Change>>()

    /**
     * The current transaction level.
     *
     * @return `0` if there are no ongoing transactions.
     */
    override val transactionLevel: Int
        get() = transactions.size


    // region Command handlers

    /**
     * Return the current value for the [key].
     *
     * @return `<Key '' not set>` if there was no such key.
     */
    override operator fun get(key: String): String? = mainStore[key]


    /**
     * Return the number of keys that have the given [value].
     * Computational complexity is `O(1)` as uses a cached counters.
     */
    override fun count(value: String): Int {
        return countersStore[value] ?: 0
    }

    /**
     * Store the [value] for [key].
     *
     * @return the previous value for the [key] or `null` if there was no such key.
     */
    override operator fun set(key: String, value: String): String? {
        return handleSet(key, value, log = true)
    }

    private fun handleSet(key: String, value: String, log: Boolean): String? {
        val previousValue = mainStore.put(key, value)
        if (value != previousValue) {
            trackChanges(key, previousValue, value, log)
        }
        return previousValue
    }

    /**
     * Remove the entry for [key].
     *
     * @return the previous value for the [key] or `null` if there was no such key.
     */
    override fun delete(key: String): String? {
        return handleDelete(key, log = true)
    }

    private fun handleDelete(key: String, log: Boolean): String? {
        val previousValue = mainStore.remove(key)
        if (previousValue != null) {
            trackChanges(key, previousValue, value = null, log = log)
        }
        return previousValue
    }

    /**
     * Start a new transaction.
     */
    override fun begin() {
        transactions.add(mutableListOf())
    }

    /**
     * Complete the current transaction.
     *
     * @throws IllegalArgumentException if there is no transaction to commit.
     */
    override fun commit() {
        require(transactions.isNotEmpty()) { NO_TRANSACTION }
        // All changes are already applied to the main store.
        // Just remove the current transaction.
        val currentLog = transactions.removeLast()
        // Merge the current transaction into the parent one if any.
        transactions.peekLast()?.addAll(currentLog)
    }

    /**
     * Revert to state prior to [begin] call.
     *
     * @throws IllegalArgumentException if there is no transaction to rollback.
     */
    override fun rollback() {
        require(transactions.isNotEmpty()) { NO_TRANSACTION }
        // Revert changes from the current transaction.
        val currentLog = transactions.removeLast()
        // Rollback changes in reverse order.
        // Minor optimization:
        //  iterate over indices to avoid creating an iterator and/or reversed wrapper.
        for (i in currentLog.indices.reversed()) {
            val (key, previousValue) = currentLog[i]
            when {
                previousValue != null -> handleSet(key, previousValue, log = false)
                else -> handleDelete(key, log = false)
            }
        }
    }

    // endregion


    // region Utility methods

    private fun trackChanges(key: String, previousValue: String?, value: String?, log: Boolean) {
        if (log) {
            transactions.peekLast()?.add(Change(key, previousValue))
        }
        value?.let { incrementCounter(it) }
        previousValue?.let { decrementCounter(it) }
    }


    // use compute methods to support atomicity if a map is thread-safe.
    private fun incrementCounter(value: String) =
        countersStore.compute(value) { _, oldValue -> (oldValue ?: 0) + 1 }

    private fun decrementCounter(value: String) =
        countersStore.compute(value) { _, oldValue ->
            val k = ((oldValue ?: 0) - 1)
            if (k > 0) k else null
        }

    // endregion


    private data class Change(val key: String, val previousValue: String?)

    private companion object {
        private const val NO_TRANSACTION = "no transaction"
    }
}
