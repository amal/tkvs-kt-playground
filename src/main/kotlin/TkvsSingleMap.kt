@file:Suppress("TooManyFunctions")

package kt.tkvs

import java.util.LinkedList
import kt.tkvs.TransactionalKeyValueStore.Companion.NO_TRANSACTION

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
class TkvsSingleMap : TkvsBaseCountersStoreAware() {

    /**
     * The main key-value store.
     */
    private val mainStore = mutableMapOf<String, String>()

    /**
     * A list of ongoing transaction change logs. Each is a list of changes.
     * Multiple values mean nested transactions.
     * The last one is the current.
     */
    private val transactions = LinkedList<MutableList<Change>>()

    override val transactionLevel: Int
        get() = transactions.size


    // region Command handlers

    override operator fun get(key: String): String? = mainStore[key]


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

    override fun begin() {
        transactions.add(mutableListOf())
    }

    override fun commit() {
        require(transactions.isNotEmpty()) { NO_TRANSACTION }
        // All changes are already applied to the main store.
        // Just remove the current transaction.
        val currentLog = transactions.removeLast()
        // Merge the current transaction into the parent one if any.
        transactions.peekLast()?.addAll(currentLog)
    }

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


    private fun trackChanges(key: String, previousValue: String?, value: String?, log: Boolean) {
        if (log) {
            transactions.peekLast()?.add(Change(key, previousValue))
        }
        trackChanges(previousValue, value)
    }


    private data class Change(val key: String, val previousValue: String?)
}
