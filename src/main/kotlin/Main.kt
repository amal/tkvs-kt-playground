package kt.tkvs

fun main() {
    val store: TransactionalKeyValueStore = TkvsSingleMap()
    val parser = CommandParser()
    println("Transactional Key-Value Store CLI")
    @Suppress("LoopWithTooManyJumpStatements")
    while (true) {
        print("> ")
        val input = readlnOrNull()?.trim()
        if (input.isNullOrBlank()) {
            println(NO_INPUT)
            continue
        }
        if (Q.equals(input, ignoreCase = true) || QUIT.equals(input, ignoreCase = true)) {
            println(BYE)
            break
        }
        if (H.equals(input, ignoreCase = true) || HELP.equals(input, ignoreCase = true)) {
            println(DOCS)
            break
        }

        parser.parse(input).mapCatching { command ->
            store.processCommand(command)
        }.onFailure { e ->
            println("Error: ${e.message ?: e}")
        }.onSuccess { result ->
            result?.let(::println)
        }
    }
}

private fun TransactionalKeyValueStore.processCommand(cmd: Command): String? {
    return when (cmd) {
        is Command.Get -> get(key = cmd.key) ?: "key not set"

        is Command.Count -> count(value = cmd.value).toString()

        is Command.Set -> {
            set(cmd.key, cmd.value)
            null
        }

        is Command.Delete -> alert { delete(key = cmd.key) }

        is Command.Begin -> {
            begin()
            null
        }

        is Command.Commit -> alert { commit() }

        is Command.Rollback -> alert { rollback() }
    }
}

private fun alert(body: () -> Any?): Nothing? {
    print("Are you sure? [y/N] ")
    val input = readlnOrNull()?.trim()
    when {
        Y.equals(input, ignoreCase = true) ||
            YES.equals(input, ignoreCase = true) ||
            TRUE.equals(input, ignoreCase = true) ||
            ONE == input
        -> {
            body()
        }
    }
    return null
}


private const val BYE = "Bye!"

private const val Q = "q"
private const val QUIT = "quit"

private const val H = "h"
private const val HELP = "help"

private const val Y = "y"
private const val YES = "yes"
private const val TRUE = "true"
private const val ONE = "1"

private const val NO_INPUT =
    "No input provided. Print '$Q' or '$QUIT' to quit. '$H' or '$HELP' for help."

private val DOCS = """
Available commands:

SET <key> <value> // store the value for the key
GET <key>         // return the current value for the key
DELETE <key>      // remove the entry for the key
COUNT <value>     // return the number of keys that have the given value
BEGIN             // start a new transaction
COMMIT            // complete the current transaction
ROLLBACK          // revert to state prior to BEGIN call

""".trimIndent()
