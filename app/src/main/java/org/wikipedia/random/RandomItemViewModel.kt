package org.wikipedia.random

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.wikipedia.Constants
import org.wikipedia.dataclient.ServiceFactory
import org.wikipedia.dataclient.WikiSite
import org.wikipedia.dataclient.page.PageSummary
import org.wikipedia.util.Resource

class RandomItemViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val handler = CoroutineExceptionHandler { _, throwable ->
        _uiState.value = Resource.Error(throwable)
    }
    val wikiSite = savedStateHandle.get<WikiSite>(Constants.ARG_WIKISITE)!!
    var summary: PageSummary? = null

    private val _uiState = MutableStateFlow(Resource<PageSummary?>())
    val uiState = _uiState.asStateFlow()

    init {
        getRandomPage()
    }

    fun getRandomPage() {
        _uiState.value = Resource.Loading()
        viewModelScope.launch(handler) {
            summary = ServiceFactory.getRest(wikiSite).getRandomSummary()
            _uiState.value = Resource.Success(summary)
        }
    }
}
