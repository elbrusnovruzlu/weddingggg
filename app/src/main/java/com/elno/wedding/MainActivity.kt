package com.elno.wedding

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.elno.wedding.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        installSplashScreen()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
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
                R.id.onboardFragment, R.id.favouriteFragment, R.id.notificationFragment -> {
                    showSystemUI()
                    binding.bottomNavigationView.isVisible = false
                }
                else -> {
                    showSystemUI()
                    binding.bottomNavigationView.isVisible = true
                }
            }
        }
    }

    private fun hideSystemUI() {
        window.statusBarColor = getColor(R.color.transparent)
        WindowCompat.getInsetsController(window, binding.root).isAppearanceLightStatusBars = false
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.ime())
            //controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH
        }
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
}