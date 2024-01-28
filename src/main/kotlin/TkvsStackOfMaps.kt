package kt.tkvs

import java.util.LinkedList
import kt.tkvs.TransactionalKeyValueStore.Companion.NO_TRANSACTION

/**
 * An in-memory transactional key-value store.
 *
 * Implementation with **stack of maps**.
 *
 * * Supports nested transactions.
 * * Commit/rollback operations apply changes from the last transaction.
 * * Parallel/concurrent transactions are NOT supported.
 *
 * **WARNING**: The class is not thread-safe!
 */
class TkvsStackOfMaps : TransactionalKeyValueStore {

    /**
     * The main key-value store.
     */
    private val mainStore = LinkedList<HashMap<String, String?>>().apply {
        push(hashMapOf())
    }

    override val transactionLevel: Int
        get() = mainStore.size - 1


    // region Command handlers

    override fun get(key: String): String? = mainStore.peek()[key]

    override fun count(value: String): Int =
        mainStore.peek().values.count { it == value }

    override fun set(key: String, value: String): String? {
        val map = mainStore.peek()
        val previousValue = map[key]
        map[key] = value
        return previousValue
    }

    override fun delete(key: String): String? =
        mainStore.peek().remove(key)


    override fun begin() {
        val newTransaction = HashMap(mainStore.peek())
        mainStore.push(newTransaction)
    }

    override fun commit() {
        require(mainStore.size > 1) { NO_TRANSACTION }
        // Replace the previous transaction with the current one.
        val lastTransaction = mainStore.pop()
        mainStore.pop()
        mainStore.push(lastTransaction)
    }

    override fun rollback() {
        require(mainStore.size > 1) { NO_TRANSACTION }
        mainStore.pop()
    }

    // endregion
}
