package com.nutriwise.auratracks

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

/**
 * Main Activity for AuraTracks wellness app
 * Handles navigation between different fragments using Bottom Navigation (phones) or Side Navigation (tablets)
 */
class MainActivity : AppCompatActivity() {
    
    private var bottomNavigation: BottomNavigationView? = null
    private var sideNavigation: NavigationView? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            enableEdgeToEdge()
            setContentView(R.layout.activity_main)
            
            // Handle window insets for edge-to-edge display
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                try {
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                insets
            }
            
            setupNavigation()
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: just show a simple layout
            try {
                setContentView(android.R.layout.activity_list_item)
            } catch (fallbackException: Exception) {
                fallbackException.printStackTrace()
            }
        }
    }
    
    /**
     * Set up navigation with NavController
     * Uses bottom navigation for phones, side navigation for tablets
     * Hides navigation on onboarding and auth screens
     */
    private fun setupNavigation() {
        try {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
            val navController = navHostFragment?.navController
            
            if (navController != null) {
                // Initialize navigation views
                bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
                sideNavigation = findViewById<NavigationView>(R.id.navigation_view)
                
                // Setup navigation with controller
                bottomNavigation?.setupWithNavController(navController)
                sideNavigation?.setupWithNavController(navController)
                
                // Listen for destination changes to show/hide navigation
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    try {
                        handleNavigationVisibility(destination.id)
                    } catch (e: Exception) {
                        android.util.Log.e("MainActivity", "Error in destination change listener", e)
                    }
                }
                
                // Set initial visibility based on current destination
                try {
                    handleNavigationVisibility(navController.currentDestination?.id)
                } catch (e: Exception) {
                    android.util.Log.e("MainActivity", "Error setting initial navigation visibility", e)
                }
            } else {
                android.util.Log.w("MainActivity", "NavController is null, navigation setup failed")
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Critical error in setupNavigation", e)
            e.printStackTrace()
        }
    }
    
    /**
     * Handle navigation bar visibility based on current destination
     */
    private fun handleNavigationVisibility(destinationId: Int?) {
        try {
            val shouldShowNavigation = when (destinationId) {
                // Main app screens - show navigation
                R.id.homeFragment,
                R.id.habitsFragment,
                R.id.moodFragment,
                R.id.hydrationFragment,
                R.id.settingsFragment,
                R.id.notificationsFragment,
                R.id.userProfileFragment -> true
                
                // Onboarding and auth screens - hide navigation
                R.id.launchScreenFragment,
                R.id.onboardingScreen1Fragment,
                R.id.onboardingScreen2Fragment,
                R.id.onboardingScreen3Fragment,
                R.id.signUpFragment,
                R.id.signInFragment -> false
                
                // Default to hide for unknown destinations
                else -> false
            }
            
            // Update navigation visibility
            bottomNavigation?.visibility = if (shouldShowNavigation) View.VISIBLE else View.GONE
            sideNavigation?.visibility = if (shouldShowNavigation) View.VISIBLE else View.GONE
            
            // Update fragment container constraints to adjust for navigation visibility
            updateFragmentConstraints(shouldShowNavigation)
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Update fragment container constraints based on navigation visibility
     */
    private fun updateFragmentConstraints(showNavigation: Boolean) {
        try {
            val navHostFragment = findViewById<View>(R.id.nav_host_fragment)
            val layoutParams = navHostFragment?.layoutParams as? ConstraintLayout.LayoutParams
            
            layoutParams?.let { params ->
                // Handle phone layout (bottom navigation)
                if (bottomNavigation != null) {
                    if (showNavigation) {
                        // When bottom navigation is visible, constrain to top of it
                        params.bottomToTop = R.id.bottom_navigation
                        params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                    } else {
                        // When bottom navigation is hidden, constrain to bottom of parent
                        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        params.bottomToTop = ConstraintLayout.LayoutParams.UNSET
                    }
                }
                
                // Handle tablet layout (side navigation) - fragment always takes full height
                // Side navigation doesn't affect fragment container constraints
                
                navHostFragment.layoutParams = params
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}