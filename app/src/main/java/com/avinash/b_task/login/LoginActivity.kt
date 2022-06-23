package com.avinash.b_task.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.avinash.b_task.R
import com.avinash.b_task.dashboard.MainActivity
import com.avinash.b_task.databinding.ActivityLoginBinding
import com.avinash.b_task.login.ui.main.LoginFragment
import com.avinash.b_task.utils.SessionManager

private lateinit var binding: ActivityLoginBinding
class LoginActivity : AppCompatActivity() {

    override fun onStart() {
        checkSession()
        super.onStart()
    }

    private fun checkSession() {
        val sessionManager = SessionManager(this)
        val session = sessionManager.findOne()
        if(session!=null){
            var intent: Intent? = null
            if (session.isSessionActive && session!!.user!!.username.length>0) {
                intent = Intent(this, MainActivity::class.java)
                this.startActivity(intent)
                this.finish()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .commitNow()
        }
    }
}