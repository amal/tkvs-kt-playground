package kt.tkvs

abstract class TkvsBaseCountersStoreAware : TransactionalKeyValueStore {

    /**
     * Value counters to support effective [Command.Count] operations.
     */
    private val countersStore = mutableMapOf<String, Int>()


    /**
     * Return the number of keys that have the given [value].
     * Computational complexity is `O(1)` as uses a cached counters.
     */
    override fun count(value: String): Int = countersStore[value] ?: 0


    protected fun trackChanges(previousValue: String?, value: String?) {
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
}
