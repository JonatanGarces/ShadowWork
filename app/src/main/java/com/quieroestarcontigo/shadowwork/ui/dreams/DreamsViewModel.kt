package com.quieroestarcontigo.shadowwork.ui.dreams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.quieroestarcontigo.shadowwork.data.model.Dream
import com.quieroestarcontigo.shadowwork.data.repo.DreamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DreamViewModel @Inject constructor(
    private val repository: DreamRepository,
) : ViewModel() {


    val dreams: StateFlow<List<Dream>> = repository.getAllDreams()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val unsyncedCount: StateFlow<Int> = repository.getUnsyncedCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    val isLoading = MutableStateFlow(false)


    fun addDream(content: String, tags: List<String>) {
        val dream = Dream(
            content = content,
            tags = tags.joinToString(","),
            synced = false,
            title = "", // optional
            description = ""
        )

        viewModelScope.launch {
            repository.insertDream(dream)
            repository.enqueueSyncWork()
        }
    }


    fun syncDreams() {
        viewModelScope.launch {
            isLoading.value = true
            repository.syncWithSupabase()
            isLoading.value = false
        }
    }
}
