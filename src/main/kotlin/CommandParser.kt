package kt.tkvs

import java.util.Locale

class CommandParser {
    private companion object {
        private val WS = "\\s+".toRegex()
    }

    fun parse(input: String): Result<Command> {
        return runCatching {
            val parts = input.trim().split(WS, limit = 3)
            val command = parts.getOrNull(0)?.uppercase(Locale.US)
            val args = parts.drop(1)
            when (command) {
                "SET" -> handleSet(args)
                "GET" -> handleGet(args)
                "DELETE" -> handleDelete(args)
                "COUNT" -> handleCount(args)
                "BEGIN" -> handleBegin(args)
                "COMMIT" -> handleCommit(args)
                "ROLLBACK" -> handleRollback(args)
                else -> error("Unknown command: $command")
            }
        }
    }

    private fun handleSet(args: List<String>): Command {
        require(args.size == 2) { "SET command requires 2 arguments: key and value" }
        return Command.Set(args[0], args[1])
    }

    private fun handleGet(args: List<String>): Command {
        require(args.size == 1) { "GET command requires 1 argument: key" }
        return Command.Get(args[0])
    }

    private fun handleDelete(args: List<String>): Command {
        require(args.size == 1) { "DELETE command requires 1 argument: key" }
        return Command.Delete(args[0])
    }

    private fun handleCount(args: List<String>): Command {
        require(args.size == 1) { "COUNT command requires 1 argument: value" }
        return Command.Count(args[0])
    }

    private fun handleBegin(args: List<String>): Command {
        require(args.isEmpty()) { "BEGIN command does not take any arguments" }
        return Command.Begin
    }

    private fun handleCommit(args: List<String>): Command {
        require(args.isEmpty()) { "COMMIT command does not take any arguments" }
        return Command.Commit
    }

    private fun handleRollback(args: List<String>): Command {
        require(args.isEmpty()) { "ROLLBACK command does not take any arguments" }
        return Command.Rollback
    }
}
