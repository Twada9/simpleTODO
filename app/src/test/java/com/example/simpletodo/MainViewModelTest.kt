package com.example.simpletodo

import com.example.simpletodo.DataAccess.TodoDao
import com.example.simpletodo.Model.Todo
import com.example.simpletodo.ViewModel.LatestTodoListUiState
import com.example.simpletodo.ViewModel.MainViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test

import org.junit.Assert.*
import java.util.UUID

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @Test
    fun startObservingTodo_Success_Tests() {
        val testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        try {
            val dao = mockk<TodoDao>()
            val list = listOf(
                Todo(
                    id = UUID.randomUUID(),
                    title = "買い物",
                    description = "牛乳とパンを買う",
                    date = System.currentTimeMillis(),
                    priority = 1
                )
            )
            every { dao.getAll() } returns MutableStateFlow(list)
            val vm = MainViewModel(dao)
            vm.startObservingTodo(testDispatcher)
            print(LatestTodoListUiState.Success(list))
            print(LatestTodoListUiState.Success(listOf()))
            val resultTodo = (vm.uiState.value as LatestTodoListUiState.Success).todo
            assertTrue(vm.uiState.value == LatestTodoListUiState.Success(list))
            assertTrue(resultTodo == LatestTodoListUiState.Success(list).todo)
            assertTrue(resultTodo != LatestTodoListUiState.Success(listOf()).todo)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun startObservingTodo_Failure_Tests() = runTest {
        val message = "Test Exception"
        val dao = mockk<TodoDao>()
        every { dao.getAll() } returns flow {
            throw Exception(message)
        }
        val vm = MainViewModel(dao)

        vm.startObservingTodo(StandardTestDispatcher(testScheduler))

        advanceUntilIdle()

        assertTrue(vm.uiState.value is LatestTodoListUiState.Error)
        val error = vm.uiState.value as LatestTodoListUiState.Error
        assertEquals(message, error.exception.message)
    }

    @Test
    fun selectTodoTests() {
        val todo = Todo(
            id = UUID.randomUUID(),
            title = "買い物",
            description = "牛乳とパンを買う",
            date = System.currentTimeMillis(),
            priority = 1
        )
        // 値の変化
        val dao = mockk<TodoDao>()
        val vm = MainViewModel(dao)
        vm.selectTodo(
            todo
        )
        assertTrue(vm.selectedTodo.value == todo)
    }

    @Test
    fun resetTodoTests() {
        // 値の変化
        val todo = Todo(
            id = UUID.randomUUID(),
            title = "買い物",
            description = "牛乳とパンを買う",
            date = System.currentTimeMillis(),
            priority = 1
        )
        val dao = mockk<TodoDao>()
        val vm = MainViewModel(dao)
        vm.selectTodo(todo)
        assertTrue(vm.selectedTodo.value == todo)

        vm.resetTodo()
        assertTrue(vm.selectedTodo.value != todo)
    }

    @Test
    fun closeModalView() {
        // 値の変化
        val dao = mockk<TodoDao>()
        val vm = MainViewModel(dao)
        vm.closeModalView()
        assertFalse(vm.showModalView.value)
    }

    @Test
    fun openModalView() {
        // 値の変化
        val dao = mockk<TodoDao>()
        val vm = MainViewModel(dao)
        vm.openModalView()
        assertTrue(vm.showModalView.value)
    }
}