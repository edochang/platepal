package com.example.platepal.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.platepal.auth.User
import com.example.platepal.auth.invalidUser

private const val TAG = "AuthViewModel"
class AuthViewModel: ViewModel() {
    // Track current authenticated user
    private var currentAuthUser = invalidUser

    // MainActivity gets updates on this via live data and informs view model
    fun setCurrentAuthUser(user: User) {
        currentAuthUser = user
    }
}