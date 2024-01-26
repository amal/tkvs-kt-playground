package kt.tkvs

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class TransactionalKeyValueStoreTest {

    private lateinit var store: TransactionalKeyValueStore

    @BeforeTest
    fun setUp() {
        store = TransactionalKeyValueStore()
    }

    @AfterTest
    fun tearDown() {
        assertEquals(0, store.transactionLevel)
    }


    @Test
    fun testSetAndGet() {
        assertNull(store.set("key1", "value1"))
        assertEquals("value1", store["key1"])
    }

    @Test
    fun testDelete() {
        store["key1"] = "value1"
        assertEquals("value1", store.delete("key1"))
        assertNull(store["key1"])
    }

    @Test
    fun testCount() {
        store["key1"] = "value"
        store["key2"] = "value"
        assertEquals(2, store.count("value"))
    }

    @Test
    fun testBeginAndRollback() {
        store["key"] = "value1"
        store.begin()
        store["key"] = "value2"
        store.rollback()
        assertEquals("value1", store["key"])
    }

    @Test
    fun testNestedTransactions() {
        val k = "key"
        val v1 = "value1"
        store[k] = v1
        assertEquals(v1, store[k])
        assertEquals(1, store.count(v1))
        assertEquals(0, store.transactionLevel)

        store.begin()
        val v2 = "value2"
        store[k] = v2
        assertEquals(v2, store[k])
        assertEquals(0, store.count(v1))
        assertEquals(1, store.count(v2))
        assertEquals(1, store.transactionLevel)

        store.begin()
        val v3 = "value3"
        store[k] = v3
        assertEquals(v3, store[k])
        assertEquals(0, store.count(v1))
        assertEquals(0, store.count(v2))
        assertEquals(1, store.count(v3))
        assertEquals(2, store.transactionLevel)
        store.commit()
        assertEquals(v3, store[k])
        assertEquals(1, store.count(v3))
        assertEquals(1, store.transactionLevel)

        store.rollback()
        assertEquals(v1, store[k])
        assertEquals(1, store.count(v1))
        assertEquals(0, store.count(v2))
        assertEquals(0, store.count(v3))
        assertEquals(0, store.transactionLevel)
    }

    @Test
    fun testCommitWithoutTransaction() {
        assertFailsWith<IllegalArgumentException> { store.commit() }
    }

    @Test
    fun testRollbackWithoutTransaction() {
        assertFailsWith<IllegalArgumentException> { store.rollback() }
    }

    @Test
    fun testCounterAfterRollback() {
        val k = "key"
        val v = "value"
        store[k] = v
        store.begin()
        store.delete(k)
        assertEquals(0, store.count(v))
        store.rollback()
        assertEquals(1, store.count(v))
    }

    @Test
    fun testCounterAfterDelete() {
        val v = "value"
        val k1 = "key1"
        store[k1] = v
        val k2 = "key2"
        store[k2] = v
        assertEquals(2, store.count(v))
        store.delete(k1)
        assertEquals(1, store.count(v))
        store.begin()
        store.delete(k2)
        assertEquals(0, store.count(v))
        store.rollback()
        assertEquals(1, store.count(v))
    }

    @Test
    fun testCounterAfterSet() {
        store["key"] = "value"
        store["key"] = "value2"
        assertEquals(1, store.count("value2"))
        assertEquals(0, store.count("value"))
    }

    @Test
    fun testCounterAfterSetAndDeleteInTransaction() {
        store["key"] = "value"
        store.begin()
        store["key"] = "value2"
        store.delete("key")
        store.rollback()
        assertEquals(1, store.count("value"))
        assertEquals(0, store.count("value2"))
    }


    @Test
    fun testSetAndGetFromTd() {
        val v = "123"
        store["foo"] = v
        assertEquals(v, store["foo"])
    }

    @Test
    fun testDeleteFromTd() {
        store["foo"] = "123"
        store.delete("foo")
        assertEquals(null, store["foo"])
    }

    @Test
    fun testCountNumberOfOccurrencesFromTd() {
        val v1 = "123"
        val v2 = "456"
        store["foo"] = v1
        store["bar"] = v2
        store["baz"] = v1
        assertEquals(2, store.count(v1))
        assertEquals(1, store.count(v2))
    }

    @Test
    fun testCommitTransactionFromTd() {
        val bar = "bar"
        val barV = "123"
        store[bar] = barV
        assertEquals(barV, store[bar])

        store.begin()
        val foo = "foo"
        val fooV = "456"
        store[foo] = fooV
        assertEquals(barV, store[bar])
        store.delete(bar)
        store.commit()

        // no transaction
        assertFailsWith<IllegalArgumentException> { store.rollback() }

        assertNull(store[bar])
        assertEquals(fooV, store[foo])
    }

    @Test
    fun testRollbackTransactionFromTd() {
        val fooV = "123"
        val foo = "foo"
        store[foo] = fooV
        val bar = "bar"
        val barV = "abc"
        store[bar] = barV

        store.begin()
        val fooV2 = "456"
        store[foo] = fooV2
        assertEquals(fooV2, store[foo])

        val barV2 = "def"
        store[bar] = barV2
        assertEquals(barV2, store[bar])

        store.rollback()
        assertEquals(fooV, store[foo])
        assertEquals(barV, store[bar])

        // no transaction
        assertFailsWith<IllegalArgumentException> { store.commit() }
    }

    @Test
    fun testNestedTransactionsFromTd() {
        val foo = "foo"
        val fooV = "123"
        store[foo] = fooV
        val bar = "bar"
        val barV = "456"
        store[bar] = barV

        store.begin()
        store[foo] = barV

        store.begin()
        assertEquals(2, store.count(barV))
        assertEquals(barV, store[foo])

        val fooV2 = "789"
        store[foo] = fooV2
        assertEquals(fooV2, store[foo])

        store.rollback()
        assertEquals(barV, store[foo])
        store.delete(foo)
        assertNull(store[foo])

        store.rollback()
        assertEquals(fooV, store[foo])
    }


    @Test
    fun testEmptyStore() {
        assertEquals(null, store["key"])
        assertEquals(0, store.count("value"))
        assertEquals(0, store.transactionLevel)
    }

    @Test
    fun testLargeStore() {
        for (i in 1..10000) {
            store["key$i"] = "value$i"
        }
        assertEquals("value5000", store["key5000"])
        assertEquals(1, store.count("value5000"))
    }

    @Test
    fun testLargeCount() {
        val n = 10000
        val v = "value"
        for (i in 1..n) {
            store["key$i"] = v
        }
        assertEquals(v, store["key5000"])
        assertEquals(n, store.count(v))
    }

    @Test
    fun testDuplicateValues() {
        val v = "value"
        for (i in 1..10) {
            store["key1"] = v
            store["key2"] = v
        }
        assertEquals(2, store.count(v))
    }

    @Test
    fun testSpecialCharactersAndLongStrings() {
        val longString = "a".repeat(10000)
        store["key!"] = "value!"
        store[longString] = longString
        assertEquals("value!", store["key!"])
        assertEquals(longString, store[longString])
    }
}
