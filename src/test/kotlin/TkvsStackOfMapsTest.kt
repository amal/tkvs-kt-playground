package kt.tkvs

import kotlin.test.BeforeTest

@Suppress("IdentifierGrammar")
class TkvsStackOfMapsTest : TransactionalKeyValueStoreTestBase() {

    override lateinit var store: TransactionalKeyValueStore

    @BeforeTest
    fun setUp() {
        store = TkvsStackOfMaps()
    }
}
