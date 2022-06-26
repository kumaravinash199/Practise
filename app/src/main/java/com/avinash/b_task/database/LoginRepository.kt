package com.avinash.b_task.database

import android.util.Log
import androidx.lifecycle.LiveData

class LoginRepository(private val dao: UserDao) {

    val users = dao.getUser()
    fun insert(user: UserEntity) {
        return dao.insertUser(user)
    }

    fun getUserName(userName: String):UserEntity?{
        return dao.getUsername(userName)
    }
     fun deleteAll(): Int {
        return dao.deleteAll()
    }

}