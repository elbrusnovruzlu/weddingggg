package com.elno.wedding

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.elno.wedding.common.Constants.ACTION
import com.elno.wedding.common.Constants.DESCRIPTION
import com.elno.wedding.common.Constants.IMAGE_URL
import com.elno.wedding.common.Constants.NOTIFICATION_LIST
import com.elno.wedding.common.Constants.NOTIFICATION_MODEL
import com.elno.wedding.common.Constants.TITLE
import com.elno.wedding.common.Constants.VENDOR_ID
import com.elno.wedding.common.LocaleManager
import com.elno.wedding.data.local.LocalDataStore
import com.elno.wedding.databinding.ActivityMainBinding
import com.elno.wedding.domain.model.NotificationModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        installSplashScreen()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val sharedPref = getSharedPreferences("sharedFile", Context.MODE_PRIVATE)
        val isOnboardShowed = sharedPref.getBoolean("isOnboardShowed", false)
        if(isOnboardShowed) {
            val graph = navController.graph
            graph.startDestination = R.id.dashboardFragment
            navController.setGraph(graph, intent.extras)
        }
        setContentView(binding.root)
        setupWithNavController(binding.bottomNavigationView, navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
             when(destination.id) {
                R.id.offerInfoFragment -> {
                    hideSystemUI()
                    binding.bottomNavigationView.isVisible = false
                }
                R.id.onboardFragment, R.id.favouriteFragment, R.id.notificationFragment, R.id.contactUsFragment, R.id.languageFragment -> {
                    showSystemUI()
                    binding.bottomNavigationView.isVisible = false
                }
                else -> {
                    showSystemUI()
                    binding.bottomNavigationView.isVisible = true
                }
            }
        }
        handleNotification()
    }

    private fun handleNotification() {
        if(intent.extras?.containsKey(ACTION) == true) {
            val action = intent.extras?.getString(ACTION)
            val vendorId = intent.extras?.getString(VENDOR_ID)
            val imageUrl = intent.extras?.getString(IMAGE_URL)
            val title = intent.extras?.getString(TITLE)
            val description = intent.extras?.getString(DESCRIPTION)
            val notificationModel = NotificationModel(
                action = action,
                title = title,
                description = description,
                imageUrl = imageUrl,
                vendorId = vendorId,
                timestamp = System.currentTimeMillis()
            )
            LocalDataStore(this).addToList(notificationModel, NOTIFICATION_LIST)
            navController.navigate(R.id.notificationFragment, bundleOf(NOTIFICATION_MODEL to notificationModel))
        }
    }

    private fun hideSystemUI() {
        window.statusBarColor = getColor(R.color.transparent)
        WindowCompat.getInsetsController(window, binding.root).isAppearanceLightStatusBars = false
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).hide(WindowInsetsCompat.Type.ime())
    }

    fun changeColorOfStatusBar(colorResId: Int, isAppearanceLightStatusBars: Boolean) {
        window.statusBarColor = getColor(colorResId)
        WindowCompat.getInsetsController(window, binding.root).isAppearanceLightStatusBars = isAppearanceLightStatusBars
    }

    private fun showSystemUI() {
        window.statusBarColor = getColor(R.color.background)
        WindowCompat.getInsetsController(window, binding.root).isAppearanceLightStatusBars = true
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, binding.root).show(WindowInsetsCompat.Type.systemBars())
    }

    fun navigateTo(searchFragment: Int) {
        binding.bottomNavigationView.selectedItemId = searchFragment
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager(this).setLocale(this)
    }

    override fun attachBaseContext(newBase: Context?) {
        if (newBase != null)
            super.attachBaseContext(LocaleManager(newBase).setLocale(newBase))
        else
            super.attachBaseContext(newBase)
    }
}