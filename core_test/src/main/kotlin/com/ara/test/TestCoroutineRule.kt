package com.ara.test

import com.ara.aranote.util.CoroutineDispatcherProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class TestDispatcherProvider(
    testDispatcher: TestDispatcher
) : CoroutineDispatcherProvider() {
    override val main = testDispatcher
    override val default = testDispatcher
    override val io = testDispatcher
    override val unconfined = testDispatcher
}

@ExperimentalCoroutinesApi
class TestCoroutineRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
    val testDispatcherProvider: TestDispatcherProvider = TestDispatcherProvider(testDispatcher)
) : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }

    fun runTestWithTestDispatcher(block: suspend () -> Unit) =
        runTest(testDispatcher) { block() }
}
