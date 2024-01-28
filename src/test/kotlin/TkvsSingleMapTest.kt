package kt.tkvs

import kotlin.test.BeforeTest

class TkvsSingleMapTest : TransactionalKeyValueStoreTestBase() {

    override lateinit var store: TransactionalKeyValueStore

    @BeforeTest
    fun setUp() {
        store = TkvsSingleMap()
    }
}
