package com.quieroestarcontigo.shadowwork.ui.dreams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DreamEventViewModel @Inject constructor() : ViewModel() {
    private val _dreamSaved = MutableSharedFlow<Unit>(replay = 0)
    val dreamSaved: SharedFlow<Unit> = _dreamSaved

    fun notifyDreamSaved() {
        viewModelScope.launch { _dreamSaved.emit(Unit) }
    }
}
