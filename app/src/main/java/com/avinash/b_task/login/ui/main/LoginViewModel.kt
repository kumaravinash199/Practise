package com.avinash.b_task.login.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avinash.b_task.utils.Session
import com.avinash.b_task.utils.SessionManager


class LoginViewModel (private val session: SessionManager,application: Application): ViewModel() {
    // TODO: Implement the ViewModel

    val inputUsername = MutableLiveData<String>()

    val inputPassword = MutableLiveData<String>()
    private val _navigatetoMainScreen = MutableLiveData<Boolean>()

    val navigateToMainScreen: LiveData<Boolean>
        get() = _navigatetoMainScreen

    private val _errorToast = MutableLiveData<Boolean>()

    val errotoast: LiveData<Boolean>
        get() = _errorToast

    private val _errorToastUsername = MutableLiveData<Boolean>()

    val errotoastUsername: LiveData<Boolean>
        get() = _errorToastUsername

    private val _errorToastInvalidPassword = MutableLiveData<Boolean>()

    val errorToastInvalidPassword: LiveData<Boolean>
        get() = _errorToastInvalidPassword


    fun doneNavigatingMainScreen() {
        _navigatetoMainScreen.value = false
    }


    fun donetoast() {
        _errorToast.value = false
        Log.i("MYTAG", "Done taoasting ")
    }


    fun donetoastErrorUsername() {
        _errorToastUsername.value = false
        Log.i("MYTAG", "Done taoasting ")
    }

    fun donetoastInvalidPassword() {
        _errorToastInvalidPassword.value = false
        Log.i("MYTAG", "Done taoasting ")
    }

    fun loginButton() {
        if (inputUsername.value == null || inputPassword.value == null) {
            _errorToast.value = true
        } else {
            val usersNames = session.findOne()!!.user
            if (usersNames != null) {
                if (usersNames.password == inputPassword.value) {
                    inputUsername.value = null
                    inputPassword.value = null
                    _navigatetoMainScreen.value = true
                } else {
                    _errorToastInvalidPassword.value = true
                }
            } else {
                _errorToastUsername.value = true
            }
        }
    }
}