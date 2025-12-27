package com.example.simpletodo

import androidx.lifecycle.viewModelScope
import com.example.simpletodo.DataAccess.TodoDao
import com.example.simpletodo.Database.TodoDatabase
import com.example.simpletodo.Model.Todo
import com.example.simpletodo.ViewModel.LatestTodoListUiState
import com.example.simpletodo.ViewModel.MainViewModel
import com.example.simpletodo.ViewModel.Priority
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import java.io.IOException
import java.util.UUID

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExampleUnitTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun startObservingTodo_Success_Tests() {
        val testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
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