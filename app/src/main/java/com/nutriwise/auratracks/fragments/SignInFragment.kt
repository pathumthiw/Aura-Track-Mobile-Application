package com.nutriwise.auratracks.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nutriwise.auratracks.R
import com.nutriwise.auratracks.data.SharedPreferencesHelper
import com.nutriwise.auratracks.databinding.FragmentSignInBinding

/**
 * Fragment for user sign in
 * Provides email/password authentication with validation
 */
class SignInFragment : Fragment() {
    
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefsHelper: SharedPreferencesHelper
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            prefsHelper = SharedPreferencesHelper(requireContext())
            setupClickListeners()
            setupValidation()
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
            // Sign in button
            binding.signInButton.setOnClickListener {
                signIn()
            }
            
            // Skip sign in button (for demo purposes)
            binding.skipButton.setOnClickListener {
                skipSignIn()
            }
            
            // Forgot password
            binding.forgotPasswordText.setOnClickListener {
                showForgotPasswordDialog()
            }
            
            // Sign up link
            binding.signUpLink.setOnClickListener {
                navigateToSignUp()
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Setup input validation
     */
    private fun setupValidation() {
        try {
            // Email validation
            binding.editEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    validateEmail()
                }
            })
            
            // Password validation
            binding.editPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    validatePassword()
                }
            })
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Validate email input
     */
    private fun validateEmail(): Boolean {
        val email = binding.editEmail.text.toString().trim()
        return when {
            email.isEmpty() -> {
                binding.emailInputLayout.error = "Email is required"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.emailInputLayout.error = "Please enter a valid email address"
                false
            }
            else -> {
                binding.emailInputLayout.error = null
                true
            }
        }
    }
    
    /**
     * Validate password input
     */
    private fun validatePassword(): Boolean {
        val password = binding.editPassword.text.toString()
        return when {
            password.isEmpty() -> {
                binding.passwordInputLayout.error = "Password is required"
                false
            }
            password.length < 6 -> {
                binding.passwordInputLayout.error = "Password must be at least 6 characters"
                false
            }
            else -> {
                binding.passwordInputLayout.error = null
                true
            }
        }
    }
    
    /**
     * Validate all inputs
     */
    private fun validateAllInputs(): Boolean {
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()
        return isEmailValid && isPasswordValid
    }
    
    /**
     * Sign in user
     */
    private fun signIn() {
        try {
            if (!validateAllInputs()) {
                Toast.makeText(requireContext(), "Please fix the errors above", Toast.LENGTH_SHORT).show()
                return
            }
            
            val email = binding.editEmail.text.toString().trim()
            val password = binding.editPassword.text.toString()
            
            // Check if user is registered
            val registeredEmail = prefsHelper.getString("registered_email", "")
            val registeredPassword = prefsHelper.getString("registered_password", "")
            
            if (registeredEmail.isNotEmpty() && registeredPassword.isNotEmpty()) {
                // Validate against registered credentials
                if (email == registeredEmail && password == registeredPassword) {
                    // Valid credentials
                    prefsHelper.saveBoolean("is_logged_in", true)
                    prefsHelper.saveString("user_email", email)
                    prefsHelper.saveBoolean("is_guest", false)

                    val userName = prefsHelper.getString("user_name", "User")
                    Toast.makeText(requireContext(), "Welcome back, $userName! 🎉", Toast.LENGTH_SHORT).show()

                    // Navigate to home
                    findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
                } else {
                    // Invalid credentials
                    if (email != registeredEmail) {
                        binding.emailInputLayout.error = "No account found with this email"
                    } else {
                        binding.passwordInputLayout.error = "Incorrect password"
                    }
                    Toast.makeText(requireContext(), "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            } else {
                // No registered user, allow demo sign in for any valid email/password
                if (email.isNotEmpty() && password.length >= 6) {
                    prefsHelper.saveBoolean("is_logged_in", true)
                    prefsHelper.saveString("user_email", email)
                    prefsHelper.saveBoolean("is_guest", false)

                    Toast.makeText(requireContext(), "Welcome! 🎉", Toast.LENGTH_SHORT).show()

                    // Navigate to home
                    findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
                } else {
                    Toast.makeText(requireContext(), "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Sign in failed", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Skip sign in (for demo purposes)
     */
    private fun skipSignIn() {
        try {
            // Save guest login state
            prefsHelper.saveBoolean("is_logged_in", true)
            prefsHelper.saveBoolean("is_guest", true)
            
            Toast.makeText(requireContext(), "Continuing as guest", Toast.LENGTH_SHORT).show()
            
            // Navigate to home
            findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Show forgot password dialog
     */
    private fun showForgotPasswordDialog() {
        try {
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.setTitle("Forgot Password")
            builder.setMessage("Password reset functionality would be implemented here. For this demo, you can use any email and password (minimum 6 characters).")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Navigate to sign up screen
     */
    private fun navigateToSignUp() {
        try {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
