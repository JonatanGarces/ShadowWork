package com.quieroestarcontigo.shadowwork.ui.audio

import androidx.lifecycle.*
import com.quieroestarcontigo.shadowwork.data.model.AudioRecord
import com.quieroestarcontigo.shadowwork.data.model.Dream
import com.quieroestarcontigo.shadowwork.data.repo.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val repository: AudioRepository
) : ViewModel() {

    val records: StateFlow<List<AudioRecord>> = repository.getAll().stateIn(
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

    fun startRecording(): String {
        return repository.startRecording()
    }

    fun stopRecording(): AudioRecord? {
        return repository.stopRecording()
    }

    fun updateTranscription(id: Int, text: String) {
        viewModelScope.launch {
            repository.updateTranscription(id, text)
        }
    }

    fun transcribeAudio(record: AudioRecord) {
        viewModelScope.launch {
            repository.speechToText(record)
        }
    }

    fun add(audio: AudioRecord) {
        viewModelScope.launch {
            repository.insert(audio)
            repository.enqueueSyncWork()
        }
    }

}
