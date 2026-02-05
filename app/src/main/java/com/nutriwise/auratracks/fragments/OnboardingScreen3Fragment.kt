package com.nutriwise.auratracks.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nutriwise.auratracks.R
import com.nutriwise.auratracks.data.SharedPreferencesHelper
import com.nutriwise.auratracks.databinding.FragmentOnboardingScreen3Binding

/**
 * Third onboarding screen - Final call to action
 * Directs users to sign up or sign in
 */
class OnboardingScreen3Fragment : Fragment() {
    
    private var _binding: FragmentOnboardingScreen3Binding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefsHelper: SharedPreferencesHelper
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingScreen3Binding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            prefsHelper = SharedPreferencesHelper(requireContext())
            setupClickListeners()
            startAnimations()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    /**
     * Setup click listeners
     */
    private fun setupClickListeners() {
        try {
            binding.getStartedButton.setOnClickListener {
                // Mark onboarding as completed
                prefsHelper.saveOnboardingComplete(true)
                
                // Navigate to sign up
                findNavController().navigate(R.id.action_onboardingScreen3Fragment_to_signUpFragment)
            }
            
            binding.signInButton.setOnClickListener {
                // Mark onboarding as completed
                prefsHelper.saveOnboardingComplete(true)
                
                // Navigate to sign in
                findNavController().navigate(R.id.action_onboardingScreen3Fragment_to_signInFragment)
            }
            
            binding.backButton.setOnClickListener {
                findNavController().navigateUp()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Start entrance animations
     */
    private fun startAnimations() {
        try {
            // Fade in the illustration
            binding.illustrationImage.alpha = 0f
            binding.illustrationImage.animate()
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(200)
                .start()
            
            // Slide in the title
            binding.titleText.translationY = 100f
            binding.titleText.alpha = 0f
            binding.titleText.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(400)
                .start()
            
            // Slide in the description
            binding.descriptionText.translationY = 100f
            binding.descriptionText.alpha = 0f
            binding.descriptionText.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(600)
                .start()
            
            // Fade in buttons
            binding.getStartedButton.alpha = 0f
            binding.signInButton.alpha = 0f
            binding.getStartedButton.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(800)
                .start()
            binding.signInButton.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(900)
                .start()
                
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
