package com.avinash.b_task.login.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avinash.b_task.utils.Session
import com.avinash.b_task.utils.SessionManager
import com.avinash.b_task.utils.User


class LoginViewModel (private val sessionManager: SessionManager, application: Application): ViewModel() {
    // TODO: Implement the ViewModel

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
    }


    fun donetoastErrorUsername() {
        _errorToastUsername.value = false
    }

    fun donetoastInvalidPassword() {
        _errorToastInvalidPassword.value = false
    }

    fun onClick(userName:String,password:String) {
        if (userName.isEmpty() || password.isEmpty()) {
            _errorToast.value = true
        } else {
            var session=sessionManager.findOne()
            if (userName.isNotEmpty() && userName == "avinash") {
                if(password.isNotEmpty() && password == "12345") {
                    if(session==null){
                        session= Session()
                    }
                    session!!.user = User(userName, password)
                    sessionManager.persist(session)
                    _navigatetoMainScreen.value = true
                }else
                    _errorToastInvalidPassword.value = true
                } else {
                _errorToastUsername.value = true

                }
            }
    }
}