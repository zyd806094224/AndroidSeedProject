package com.demo.main.ui.mine.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.demo.network.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class EditTextInputViewModel: BaseViewModel() {

    private val _searchResults = MutableStateFlow<List<String>>(emptyList())
    val searchResults: StateFlow<List<String>> = _searchResults.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var searchJob: Job? = null

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * 开始搜索
     */
    fun searchWithQuery(query: String) {
        updateSearchQuery(query)
        performSearch()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun performSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchQuery
                .debounce(300) //避免用户输入时频繁触发网络请求
                //.filter { query -> query.isNotEmpty() } //只在输入足够字符时才触发搜索
                .distinctUntilChanged() //避免重复搜索相同内容
                //总是使用最新的搜索结果，取消之前的请求
                .flatMapLatest { query ->
                    searchNetwork(query)
                        //使用 catch 操作符处理网络错误
                        .catch { e ->
                            Log.e("Search", "Search failed: ${e.message}")
                            emit(emptyList())
                        }
                }
                .collect {
                    _searchResults.value = it
                }
        }
    }

    private fun searchNetwork(query: String): Flow<List<String>> = flow {
        // 模拟网络请求
        delay(1000)

        // 实际网络请求代码示例：
        // val response = searchApi.search(query)
        // emit(response.results)

        // 模拟数据
        val mockResults = listOf(
            "搜索结果 1: $query",
            "搜索结果 2: $query",
            "搜索结果 3: $query"
        )
        emit(mockResults)
    }.flowOn(Dispatchers.IO)
}