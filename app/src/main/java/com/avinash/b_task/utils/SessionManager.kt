package com.avinash.b_task.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class SessionManager: Repository<Session> {

    // Shared Preferences
    private lateinit var sharedPreferences:SharedPreferences

    // Editor for Shared preferences
    private lateinit var editor: SharedPreferences.Editor

    // Context
    private var context: Context? = null
    private var sessionManager: SessionManager?=null
    val PRIVATE_MODE:Int =0

companion object {
    val PREF_NAME:String ="B_TASK"

}
    constructor(context: Context) {
        this.context = context
        initSharedPreferences()
    }


    private fun initSharedPreferences() {
        sharedPreferences = context!!.getSharedPreferences(PREF_NAME,PRIVATE_MODE)
        editor = sharedPreferences.edit()
    }


    override fun persist(entity: Session?) {
        if (entity == null) {
            return
        }
        entity.isSessionActive=true
        editor!!.putString(Session.Companion.TAG, toString(entity))
        editor!!.commit()
    }

    override fun update(entity: Session?) {
        if (entity == null) {
            return
        }
        editor!!.putString(Session.Companion.TAG, toString(entity))
        editor!!.commit()
    }


    override fun findOne(): Session? {
        val sessionData = sharedPreferences!!.getString(Session.Companion.TAG, null)
        if (sessionData != null) {
            return toObject(sessionData, Session::class.java)
        } else {
        }
        return null
    }

    override fun deleteAll() {
        TODO("Not yet implemented")
    }

    fun toString(src: Any?): String? {
        if (src == null) {
            return null
        }
        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson: Gson = builder.create()
        return gson.toJson(src)
    }

    fun <T> toObject(data: String?, type: Class<T>?): T {
        val gson = Gson()
        return gson.fromJson(data, type)
    }
}