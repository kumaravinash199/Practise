package com.avinash.b_task.dashboard

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
import com.avinash.b_task.databinding.SubMenuMeasureBinding
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

    @SuppressLint("ClickableViewAccessibility")
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
            if (session.isSessionActive && session.user!!.username.isNotEmpty()) {
                navHeaderMainBinding.name.text = session.user!!.username
            }
        }
        binding.appBarMain.toolbar.setNavigationOnClickListener(View.OnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.close()
            } else {
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


                }
                intent = Intent(this, LoginActivity::class.java)
                this.startActivity(intent)
                this.finish()
            }

        })
        binding.appBarMain.contentMain.ivFullscreen.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN ->
                    if (isFullScreenMode) {
                        binding.appBarMain.contentMain.ivFullscreen.setImageResource(R.drawable.ic_full_screen_icon)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            window.insetsController?.show(WindowInsets.Type.navigationBars())
                        } else {
                            WindowCompat.setDecorFitsSystemWindows(window, true)
                            WindowInsetsControllerCompat(
                                window,
                                binding.root
                            ).let { controller ->
                                controller.show(WindowInsetsCompat.Type.navigationBars())

                            }
                        }
                        binding.appBarMain.toolbar.visibility = View.VISIBLE
                        binding.appBarMain.contentMain.menuView.visibility = View.VISIBLE
                        isFullScreenMode = false

                    } else {
                        binding.appBarMain.contentMain.ivFullscreen.setImageResource(R.drawable.ic_fullscreen_exit_icon)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            window.insetsController?.hide(WindowInsets.Type.systemBars())
                        } else {
                            WindowCompat.setDecorFitsSystemWindows(window, false)
                            WindowInsetsControllerCompat(
                                window,
                                binding.root
                            ).let { controller ->
                                controller.hide(WindowInsetsCompat.Type.systemBars())
                                controller.systemBarsBehavior =
                                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
                            }
                        }

                        binding.appBarMain.toolbar.visibility = View.GONE
                        binding.appBarMain.contentMain.menuView.visibility = View.GONE
                        isFullScreenMode = true
                    }
            }

            v?.onTouchEvent(event) ?: true
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.systemBars())
            window.insetsController?.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
        } else {
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowInsetsControllerCompat(
                window,
                binding.root
            ).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
            }
        }
        handleMenu()

    }

    private lateinit var filterPopup: PopupWindow
    private lateinit var view: SubMenuMeasureBinding
    fun handleMenu() {
        binding.appBarMain.contentMain.viewAnnotate.setOnClickListener(View.OnClickListener {
            inputDialog()
        })

        binding.appBarMain.contentMain.viewMeasure.setOnClickListener(View.OnClickListener {
            it.isSelected = !it.isSelected
            if (deviceType == "Tablet")
                binding.appBarMain.contentMain.ivArrow.rotation = 180f
            else
                binding.appBarMain.contentMain.ivArrow.rotation = 90f
            filterPopup = showPopupWindow(binding.appBarMain.contentMain.ivArrow)
            filterPopup.setOnDismissListener {
                if (deviceType == "Tablet")
                    binding.appBarMain.contentMain.ivArrow.rotation = 0f
                else
                    binding.appBarMain.contentMain.ivArrow.rotation = 270f
                binding.appBarMain.contentMain.viewMeasure.isSelected =
                    !binding.appBarMain.contentMain.viewMeasure.isSelected
            }
        })


    }


    private fun showPopupWindow(anchor: View): PopupWindow {
        PopupWindow(anchor.context).apply {
            isOutsideTouchable = true
            val inflater = LayoutInflater.from(anchor.context)
            contentView = inflater.inflate(R.layout.sub_menu_measure, null).apply {
                measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
            }
            contentView.findViewById<RelativeLayout?>(R.id.viewClear)
                .setOnClickListener(View.OnClickListener {
                    dismissPopup()
                })
            contentView.findViewById<RelativeLayout?>(R.id.viewEllipse)
                .setOnClickListener(View.OnClickListener {
                    dismissPopup()
                })
            contentView.findViewById<RelativeLayout?>(R.id.viewDistanse)
                .setOnClickListener(View.OnClickListener {
                    dismissPopup()
                })
        }.also { popupWindow ->
            popupWindow.setBackgroundDrawable(null)
        }.also { popupWindow ->
            // Absolute location of the anchor view
            val location = IntArray(2).apply {
                anchor.getLocationOnScreen(this)
            }
            val size = Size(
                popupWindow.contentView.measuredWidth,
                popupWindow.contentView.measuredHeight
            )

            if (deviceType == "Tablet") {
                val valueX = binding.appBarMain.contentMain.ivArrow.width / 2
                val valueY = binding.appBarMain.contentMain.ivArrow.height
                popupWindow.showAtLocation(
                    anchor,
                    Gravity.TOP or Gravity.START,
                    location[0] - valueX,
                    location[1] - (size.height - valueY)
                )
            } else {
                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)

                var width = displayMetrics.widthPixels
                var height = displayMetrics.heightPixels
                val valueX =
                    binding.appBarMain.contentMain.ivArrow.width - resources.getDimensionPixelSize(R.dimen.nav_header_vertical_spacing)

                if (height > 1920) {
                    val valueY =
                        binding.appBarMain.contentMain.ivArrow.height - resources.getDimensionPixelSize(
                            R.dimen.nav_header_vertical_spacing
                        )
                    popupWindow.showAtLocation(
                        anchor,
                        Gravity.TOP or Gravity.START,
                        location[0] - (size.width - valueX),
                        location[1] - (size.height + valueY)
                    )
                } else {
                    val valueY = binding.appBarMain.contentMain.ivArrow.height-resources.getDimensionPixelSize(R.dimen.menu_marginy_land)
                    popupWindow.showAtLocation(
                        anchor,
                        Gravity.TOP or Gravity.START,
                        location[0] - (size.width - valueX),
                        location[1] - (size.height+valueY)
                    )
                }
            }

            return popupWindow
        }

    }

    fun dismissPopup() {
        filterPopup.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    fun inputDialog() {
        var m_Text: String
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Input your text here")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK",
            DialogInterface.OnClickListener { dialog, _ ->
                m_Text = input.text.toString()
                val transaction = supportFragmentManager.beginTransaction()
                var annotateFragment: AnnotateFragment = AnnotateFragment.newInstance(m_Text)

                transaction.replace(R.id.nav_host_fragment_content_main, annotateFragment)
                transaction.disallowAddToBackStack()
                transaction.commit()
                dialog.dismiss()

            })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })

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