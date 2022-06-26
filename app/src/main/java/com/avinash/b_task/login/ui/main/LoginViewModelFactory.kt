package com.avinash.b_task.login.ui.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.avinash.b_task.database.LoginRepository
import com.avinash.b_task.utils.SessionManager
import java.lang.IllegalArgumentException

class LoginViewModelFactory(
    private  val repository: LoginRepository,
    private  val sessionManager: SessionManager,
    private val application: Application
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository,sessionManager, application) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}