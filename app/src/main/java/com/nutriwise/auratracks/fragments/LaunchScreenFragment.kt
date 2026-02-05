package com.nutriwise.auratracks.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nutriwise.auratracks.R
import com.nutriwise.auratracks.data.SharedPreferencesHelper
import com.nutriwise.auratracks.databinding.FragmentLaunchScreenBinding

/**
 * Launch screen fragment with animated logo and splash effect
 * Shows app logo with animations and navigates to appropriate screen
 */
class LaunchScreenFragment : Fragment() {
    
    private var _binding: FragmentLaunchScreenBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefsHelper: SharedPreferencesHelper
    private val splashDelay = 3000L // 3 seconds
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLaunchScreenBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            prefsHelper = SharedPreferencesHelper(requireContext())
            startAnimations()
            setupNavigation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    /**
     * Start logo animations
     */
    private fun startAnimations() {
        try {
            // Logo fade in animation
            val logoFadeIn = ObjectAnimator.ofFloat(binding.logoIllustration, "alpha", 0f, 1f)
            logoFadeIn.duration = 1000
            logoFadeIn.interpolator = AccelerateDecelerateInterpolator()
            
            // Logo scale animation
            val logoScaleX = ObjectAnimator.ofFloat(binding.logoIllustration, "scaleX", 0.5f, 1.2f, 1.0f)
            val logoScaleY = ObjectAnimator.ofFloat(binding.logoIllustration, "scaleY", 0.5f, 1.2f, 1.0f)
            logoScaleX.duration = 1500
            logoScaleY.duration = 1500
            logoScaleX.interpolator = AccelerateDecelerateInterpolator()
            logoScaleY.interpolator = AccelerateDecelerateInterpolator()
            
            // Tagline fade in animation (delayed)
            val taglineFadeIn = ObjectAnimator.ofFloat(binding.taglineText, "alpha", 0f, 1f)
            taglineFadeIn.duration = 800
            taglineFadeIn.startDelay = 500
            taglineFadeIn.interpolator = AccelerateDecelerateInterpolator()
            
            // Get Started button fade in
            val buttonFadeIn = ObjectAnimator.ofFloat(binding.getStartedButton, "alpha", 0f, 1f)
            buttonFadeIn.duration = 500
            buttonFadeIn.startDelay = 1500
            
            // Start all animations
            logoFadeIn.start()
            logoScaleX.start()
            logoScaleY.start()
            taglineFadeIn.start()
            buttonFadeIn.start()
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Setup navigation after delay
     */
    private fun setupNavigation() {
        try {
            // Set up button click listener
            binding.getStartedButton.setOnClickListener {
                navigateToNextScreen()
            }
            
            // Auto-navigate after delay
            Handler(Looper.getMainLooper()).postDelayed({
                if (_binding != null && isAdded) {
                    navigateToNextScreen()
                }
            }, splashDelay)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Navigate to appropriate screen based on user state
     */
    private fun navigateToNextScreen() {
        try {
            // Reset user flow for testing - REMOVE THIS FOR PRODUCTION
            prefsHelper.resetUserFlow()
            
            // For testing purposes, always start with onboarding flow
            // Comment these lines out and uncomment the logic below for production
            findNavController().navigate(R.id.action_launchScreenFragment_to_onboardingScreen1Fragment)
            return
            
            // Production logic (commented out for testing):
            /*
            val isLoggedIn = prefsHelper.getBoolean("is_logged_in", false)
            val isOnboardingComplete = prefsHelper.isOnboardingComplete()
            
            when {
                // Check if user is logged in
                isLoggedIn -> {
                    findNavController().navigate(R.id.action_launchScreenFragment_to_homeFragment)
                }
                // Check if onboarding is completed  
                isOnboardingComplete -> {
                    findNavController().navigate(R.id.action_launchScreenFragment_to_signInFragment)
                }
                // Show onboarding for new users
                else -> {
                    findNavController().navigate(R.id.action_launchScreenFragment_to_onboardingScreen1Fragment)
                }
            }
            */
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback navigation - always go to onboarding
            try {
                findNavController().navigate(R.id.action_launchScreenFragment_to_onboardingScreen1Fragment)
            } catch (fallbackError: Exception) {
                fallbackError.printStackTrace()
            }
        }
    }
}
