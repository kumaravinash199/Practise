package com.avinash.b_task.login.ui.main

import android.app.Application
import android.provider.SyncStateContract.Helpers.insert
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avinash.b_task.database.LoginRepository
import com.avinash.b_task.database.UserEntity
import com.avinash.b_task.utils.Session
import com.avinash.b_task.utils.SessionManager
import com.avinash.b_task.utils.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class LoginViewModel(
    private val repository: LoginRepository,
    private val sessionManager: SessionManager,
    application: Application
) : ViewModel() {
    // TODO: Implement the ViewModel

    private val _navigatetoMainScreen = MutableLiveData<Boolean>()

    val navigateToMainScreen: LiveData<Boolean>
        get() = _navigatetoMainScreen

    private val _errorToast = MutableLiveData<Boolean>()

    val errotoast: LiveData<Boolean>
        get() = _errorToast

    private val _errorToastInvalid = MutableLiveData<Boolean>()
    val errotoastInvalid: LiveData<Boolean>
        get() = _errorToastInvalid

    private val _errorToastUsername = MutableLiveData<Boolean>()

    val errotoastUsername: LiveData<Boolean>
        get() = _errorToastUsername

    private val _errorToastInvalidPassword = MutableLiveData<Boolean>()

    val errorToastInvalidPassword: LiveData<Boolean>
        get() = _errorToastInvalidPassword

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

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

    private fun insert(user: UserEntity): Job =  CoroutineScope(Dispatchers.IO).launch {
        repository.insert(user)
    }

    fun insertDatabase() {

        CoroutineScope(Dispatchers.IO).launch{
            insert(UserEntity(0, "avinash", "12345"))
            insert(UserEntity(0, "ram", "12345"))
            insert(UserEntity(0, "shyam", "12345"))
            insert(UserEntity(0, "hari", "12345"))

        }
    }

    fun onClick(userName: String, password: String) {
        if (userName.isEmpty() || password.isEmpty()) {
            _errorToast.value = true
        } else {
            var session = sessionManager.findOne()
            CoroutineScope(Dispatchers.IO).launch {
                    val usersNames = repository.getUserName(userName!!)
                    if (usersNames != null) {
                        if (userName == usersNames.userName) {
                            if (password == usersNames.password) {
                                if (session == null) {
                                    session = Session()
                                }
                                session!!.user = User(userName, password)
                                sessionManager.persist(session)
                                _navigatetoMainScreen.postValue(true)
                            } else
                                _errorToastInvalidPassword.postValue(true)
                        } else
                            _errorToastUsername.postValue(true)
                    } else {
                        _errorToastInvalid.postValue(true)

                    }
            }
        }
    }
}