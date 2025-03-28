package com.quieroestarcontigo.shadowwork.ui.auth

import androidx.lifecycle.*
import com.quieroestarcontigo.shadowwork.data.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {
    val session = repo.session
    val isAuthenticated = MutableLiveData<Boolean>()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            isAuthenticated.value = repo.login(email, pass)
        }
    }

    fun signup(email: String, password: String) {
        viewModelScope.launch {
            isAuthenticated.value = repo.signup(email, password)
        }
    }
    fun tryRefresh() {
        viewModelScope.launch {
            repo.refresh()
        }
    }
    fun logout() {
        viewModelScope.launch {
            repo.logout()
            isAuthenticated.value = false
        }
    }


}
