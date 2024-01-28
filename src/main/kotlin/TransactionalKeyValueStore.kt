package kt.tkvs

interface TransactionalKeyValueStore {
    val transactionLevel: Int


    operator fun get(key: String): String?

    fun count(value: String): Int

    operator fun set(key: String, value: String): String?

    fun delete(key: String): String?


    fun begin()

    fun commit()

    fun rollback()
}
