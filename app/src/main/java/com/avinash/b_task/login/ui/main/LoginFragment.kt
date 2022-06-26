package com.avinash.b_task.login.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.room.RoomDatabase
import com.avinash.b_task.dashboard.MainActivity
import com.avinash.b_task.database.LoginRepository
import com.avinash.b_task.database.UserDatabase
import com.avinash.b_task.databinding.FragmentLoginBinding
import com.avinash.b_task.utils.Repository
import com.avinash.b_task.utils.Session
import com.avinash.b_task.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding
    private lateinit var database: RoomDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // TODO: Use the ViewModel
        val application = requireNotNull(this.activity).application
        val dao = UserDatabase.getInstance(application).userDao()
        val repository = LoginRepository(dao)
        val sessionManager=SessionManager(application)

        val factory = LoginViewModelFactory(repository, sessionManager, application)

        loginViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
        var session = sessionManager.findOne()
        if (session == null) {
            session = Session()
        }
        if (!session.isDatabseCreated) {
            CoroutineScope(Dispatchers.IO).launch {
                loginViewModel.insertDatabase()
            }
        }

        session.isDatabseCreated=true
       sessionManager.persist(session)
        inItObserver()
    }

    fun inItObserver() {

        loginViewModel.errotoast.observe(viewLifecycleOwner, Observer { hasError ->
            if (hasError == true) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
                loginViewModel.donetoast()
            }
        })

        loginViewModel.errotoastInvalid.observe(viewLifecycleOwner, Observer { hasError ->
            if (hasError == true) {
                Toast.makeText(requireContext(), "Username as avinash and password 12345", Toast.LENGTH_SHORT)
                    .show()
                loginViewModel.donetoast()
            }
        })

        loginViewModel.errotoastUsername.observe(viewLifecycleOwner, Observer { hasError ->
            if (hasError == true) {
                Toast.makeText(
                    requireContext(),
                    "User doesn't exist, enter avinash as username",
                    Toast.LENGTH_SHORT
                ).show()
                loginViewModel.donetoastErrorUsername()
            }
        })

        loginViewModel.errorToastInvalidPassword.observe(viewLifecycleOwner, Observer { hasError ->
            if (hasError == true) {
                Toast.makeText(requireContext(), "Please enter password 12345", Toast.LENGTH_SHORT)
                    .show()
                loginViewModel.donetoastInvalidPassword()
            }
        })

        loginViewModel.navigateToMainScreen.observe(viewLifecycleOwner, Observer { hasFinished ->
            if (hasFinished == true) {
                loginViewModel.doneNavigatingMainScreen()
                navigateDashboard()
            }
        })
        binding.btnLogin.setOnClickListener(View.OnClickListener {
            onClick()
        })
    }

    private fun navigateDashboard() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }

    fun onClick(){
        var username:String=binding.etUsername.text.toString()
        var password:String = binding.etPassword.text.toString()
        loginViewModel.onClick(username,password)
    }


}
