package com.avinash.b_task.utils

import java.io.Serializable

class Session : Serializable {

    companion object {
        val TAG = SessionManager::class.simpleName!!
    }

    var user: User? = null
    var isSessionActive: Boolean = false
}
