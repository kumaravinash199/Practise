package com.avinash.b_task.dashboard

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.WindowInsets
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.RoomDatabase
import com.avi.AnnotateFragment
import com.avinash.b_task.R
import com.avinash.b_task.database.LoginRepository
import com.avinash.b_task.database.UserDatabase
import com.avinash.b_task.databinding.ActivityMainBinding
import com.avinash.b_task.databinding.NavHeaderMainBinding
import com.avinash.b_task.login.LoginActivity
import com.avinash.b_task.utils.SessionManager
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.math.sqrt


class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHeaderMainBinding: NavHeaderMainBinding
    private var deviceType = "Mobile"
    private var isFullScreenMode = false
    private lateinit var database: RoomDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceType = getDeviceInfo(this, Device.DEVICE_TYPE)
        requestedOrientation = if (deviceType == "Tablet")
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        else
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityMainBinding.inflate(layoutInflater)
        navHeaderMainBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))
        setContentView(binding.root)
        initViews()
    }

    fun initViews() {
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        setSupportActionBar(binding.appBarMain.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val sessionManager = SessionManager(this)
        val session = sessionManager.findOne()
        if (session != null) {
            if (session.isSessionActive && session!!.user!!.username.length > 0) {
                navHeaderMainBinding.name.text = session.user!!.username
            }
        }
        binding.appBarMain.toolbar.setNavigationOnClickListener(View.OnClickListener {
            if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.close()
            }else{
                drawerLayout.open()
            }
        })
        navHeaderMainBinding.logout.setOnClickListener(View.OnClickListener {
//            Toast.makeText(applicationContext,"log out clicked",Toast.LENGTH_SHORT).show()
            if (session != null) {

                launch {
                    sessionManager.deleteAll()
                    val dao = UserDatabase.getInstance(application).userDao()
                    val repository = LoginRepository(dao)
                    CoroutineScope(Dispatchers.IO).launch {
                        repository.deleteAll()
                    }
                    var intent: Intent? = null


                }
                intent = Intent(this, LoginActivity::class.java)
                this.startActivity(intent)
                this.finish()
            }

        })
        binding.appBarMain.contentMain.ivFullscreen!!.setOnClickListener(View.OnClickListener {
            if (isFullScreenMode) {
                binding.appBarMain.contentMain.ivFullscreen!!.setImageResource(R.drawable.ic_full_screen_icon)
                @Suppress("DEPRECATION")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.insetsController?.show(WindowInsets.Type.statusBars())
                } else {
                    val decorView = window.decorView
                    val uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    decorView.systemUiVisibility = uiOptions
                }
                binding.appBarMain.toolbar.visibility = View.VISIBLE
                binding.appBarMain.contentMain.menuView.visibility = View.VISIBLE
                isFullScreenMode = false

            } else {
                binding.appBarMain.contentMain.ivFullscreen!!.setImageResource(R.drawable.ic_fullscreen_exit_icon)
                @Suppress("DEPRECATION")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.insetsController?.hide(WindowInsets.Type.statusBars())
                } else {
                    val decorView = window.decorView
                    val uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    decorView.systemUiVisibility = uiOptions

                }

                binding.appBarMain.toolbar.visibility = View.GONE
                binding.appBarMain.contentMain.menuView.visibility = View.GONE
                isFullScreenMode = true
            }
        })
        handleMenu()

    }

    fun handleMenu() {
        binding.appBarMain.contentMain.viewAnnotate.setOnClickListener(View.OnClickListener {


            inputDialog()
        })

        binding.appBarMain.contentMain.viewMeasure.setOnClickListener(View.OnClickListener {
            it.isSelected = !it.isSelected

        })

    }

    fun inputDialog() {
        var m_Text: String = ""
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Input your text here")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK",
            DialogInterface.OnClickListener { dialog, which ->
                m_Text = input.text.toString()
                val transaction = supportFragmentManager.beginTransaction()
                var annotateFragment:AnnotateFragment= AnnotateFragment.newInstance(m_Text)

                transaction.replace(R.id.nav_host_fragment_content_main, annotateFragment)
                transaction.disallowAddToBackStack()
                transaction.commit()
                dialog.dismiss()

            })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    enum class Device {
        DEVICE_TYPE
    }

    companion object {
        fun getDeviceInfo(context: Context, device: Device?): String {
            try {
                when (device) {
                    Device.DEVICE_TYPE -> return if (isTablet(context)) {
                        if (getDevice5inch(context)) {
                            "Tablet"
                        } else {
                            "Mobile"
                        }
                    } else {
                        "Mobile"
                    }
                    else -> {

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

        private fun getDevice5inch(context: Context): Boolean {
            return try {
                val displayMetrics = context.resources.displayMetrics
                val yInch = displayMetrics.heightPixels / displayMetrics.ydpi
                val xInch = displayMetrics.widthPixels / displayMetrics.xdpi
                val diagonalInch = sqrt(xInch * xInch + yInch * yInch.toDouble())

                diagonalInch >= 7

            } catch (e: Exception) {
                false
            }

        }

        private fun isTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
        }
    }

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

}