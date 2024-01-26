package kt.tkvs

sealed interface Command {

    /** Return the current value for the [key] */
    data class Get(val key: String) : Command

    /** Return the number of keys that have the given [value] */
    data class Count(val value: String) : Command

    /** Store the [value] for [key] */
    data class Set(val key: String, val value: String) : Command

    /** Remove the entry for [key] */
    data class Delete(val key: String) : Command


    /** Start a new transaction */
    data object Begin : Command

    /** Complete the current transaction */
    data object Commit : Command

    /** Revert to state prior to [Begin] call */
    data object Rollback : Command
}
