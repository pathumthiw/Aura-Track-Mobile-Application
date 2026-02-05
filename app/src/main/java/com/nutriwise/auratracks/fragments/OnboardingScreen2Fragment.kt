package com.nutriwise.auratracks.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nutriwise.auratracks.R
import com.nutriwise.auratracks.databinding.FragmentOnboardingScreen2Binding

/**
 * Second onboarding screen - Daily Path to Wellness
 * Shows daily wellness illustration with motivational message
 */
class OnboardingScreen2Fragment : Fragment() {
    
    private var _binding: FragmentOnboardingScreen2Binding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingScreen2Binding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
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
            binding.nextButton.setOnClickListener {
                findNavController().navigate(R.id.action_onboardingScreen2Fragment_to_onboardingScreen3Fragment)
            }
            
            binding.skipButton.setOnClickListener {
                findNavController().navigate(R.id.action_onboardingScreen2Fragment_to_onboardingScreen3Fragment)
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
            binding.nextButton.alpha = 0f
            binding.skipButton.alpha = 0f
            binding.nextButton.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(800)
                .start()
            binding.skipButton.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(800)
                .start()
                
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
