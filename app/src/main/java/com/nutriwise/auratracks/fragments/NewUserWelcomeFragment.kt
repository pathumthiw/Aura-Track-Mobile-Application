package com.nutriwise.auratracks.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nutriwise.auratracks.R
import com.nutriwise.auratracks.data.SharedPreferencesHelper
import com.nutriwise.auratracks.databinding.FragmentNewUserWelcomeBinding

/**
 * Welcome screen specifically for new users
 * Introduces features and guides them through initial setup
 */
class NewUserWelcomeFragment : Fragment() {
    
    private var _binding: FragmentNewUserWelcomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefsHelper: SharedPreferencesHelper
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewUserWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            prefsHelper = SharedPreferencesHelper(requireContext())
            setupClickListeners()
            
            // Mark user data as exists after they see the welcome
            prefsHelper.markUserDataExists()
            
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback navigation if there's an error
            navigateToHome()
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
        binding.getStartedButton.setOnClickListener {
            handleGetStarted()
        }
    }
    
    /**
     * Handle get started button press
     */
    private fun handleGetStarted() {
        try {
            // Mark welcome screen as complete
            prefsHelper.completeWelcomeScreen()
            
            // Show a friendly message
            Toast.makeText(requireContext(), "Welcome! Let's start your wellness journey! 🌟", Toast.LENGTH_LONG).show()
            
            // Navigate to home with new user experience
            navigateToHome()
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Something went wrong, but let's continue!", Toast.LENGTH_SHORT).show()
            navigateToHome()
        }
    }
    
    /**
     * Navigate to home screen with appropriate data
     */
    private fun navigateToHome() {
        try {
            findNavController().navigate(R.id.action_newUserWelcomeFragment_to_homeFragment)
        } catch (e: Exception) {
            e.printStackTrace()
            // If navigation fails, try a simpler approach
            try {
                findNavController().navigate(R.id.homeFragment)
            } catch (fallbackError: Exception) {
                fallbackError.printStackTrace()
            }
        }
    }
}
