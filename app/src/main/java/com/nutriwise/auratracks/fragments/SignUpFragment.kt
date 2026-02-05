package com.nutriwise.auratracks.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nutriwise.auratracks.R
import com.nutriwise.auratracks.data.SharedPreferencesHelper
import com.nutriwise.auratracks.databinding.FragmentSignUpBinding
import com.nutriwise.auratracks.models.UserProfile
import java.text.SimpleDateFormat
import java.util.*

/**
 * Modern Sign up fragment with simplified validation and user profile creation
 * Collects basic user information and creates account
 */
class SignUpFragment : Fragment() {
    
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefsHelper: SharedPreferencesHelper
    private var isPasswordVisible = false
    private var selectedBirthday: Date? = null
    private var selectedGender: String = ""
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            prefsHelper = SharedPreferencesHelper(requireContext())
            setupClickListeners()
            setupBackButton()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    /**
     * Setup back button
     */
    private fun setupBackButton() {
        try {
            binding.backButton.setOnClickListener {
                findNavController().navigateUp()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Setup click listeners
     */
    private fun setupClickListeners() {
        try {
            // Password toggle visibility
            binding.passwordToggle.setOnClickListener {
                togglePasswordVisibility()
            }
            
            // Sign up button
            binding.signUpButton.setOnClickListener {
                performSignUp()
            }
            
            // Gender picker
            binding.editGender.setOnClickListener {
                showGenderPicker()
            }
            
            // Birthday picker
            binding.editBirthday.setOnClickListener {
                showDatePicker()
            }
            
            // Sign in link
            binding.signinLink.setOnClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Toggle password visibility
     */
    private fun togglePasswordVisibility() {
        try {
            isPasswordVisible = !isPasswordVisible
            
            if (isPasswordVisible) {
                binding.editPassword.inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.passwordToggle.setImageResource(R.drawable.ic_visibility_off)
            } else {
                binding.editPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.passwordToggle.setImageResource(R.drawable.ic_visibility)
            }
            
            // Move cursor to end of text
            binding.editPassword.setSelection(binding.editPassword.text?.length ?: 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Show date picker for birthday
     */
    private fun showDatePicker() {
        try {
            val calendar = Calendar.getInstance()
            
            // Set default to 25 years ago
            calendar.add(Calendar.YEAR, -25)
            
            selectedBirthday?.let {
                calendar.time = it
            }
            
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                    selectedBirthday = selectedCalendar.time
                    binding.editBirthday.setText(dateFormat.format(selectedCalendar.time))
                },
                year, month, day
            )
            
            // Set max date to today
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            
            // Set min date to 120 years ago
            val minCalendar = Calendar.getInstance()
            minCalendar.add(Calendar.YEAR, -120)
            datePickerDialog.datePicker.minDate = minCalendar.timeInMillis
            
            // Set title and show
            datePickerDialog.setTitle("Select Your Birthday")
            datePickerDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Show gender picker dialog
     */
    private fun showGenderPicker() {
        try {
            val genders = arrayOf("Male", "Female", "Other", "Prefer not to say")
            val genderNames = genders.map { it }.toTypedArray()
            
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Gender")
                .setItems(genderNames) { _, which ->
                    selectedGender = genders[which]
                    binding.editGender.setText(selectedGender)
                }
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Validate all input fields
     */
    private fun validateInput(): Boolean {
        var isValid = true
        
        // Full Name validation
        val name = binding.editFullName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Full name is required", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (name.length < 2) {
            Toast.makeText(requireContext(), "Name must be at least 2 characters", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        
        // Email validation
        val email = binding.editEmail.text.toString().trim()
        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Email is required", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        
        // Password validation
        val password = binding.editPassword.text.toString()
        if (password.isEmpty()) {
            Toast.makeText(requireContext(), "Password is required", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (password.length < 8) {
            Toast.makeText(requireContext(), "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        
        // Confirm Password validation
        val confirmPassword = binding.editConfirmPassword.text.toString()
        if (confirmPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Please confirm your password", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (password != confirmPassword) {
            Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        
        // Gender validation
        if (selectedGender.isEmpty()) {
            Toast.makeText(requireContext(), "Please select your gender", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        
        // Birthday validation
        // Note: Birthday is optional, but we validate it if provided
        if (selectedBirthday != null) {
            val calendar = Calendar.getInstance()
            calendar.time = selectedBirthday!!
            val birthYear = calendar.get(Calendar.YEAR)
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val calculatedAge = currentYear - birthYear
            
            if (calculatedAge < 13) {
                Toast.makeText(requireContext(), "You must be at least 13 years old to sign up", Toast.LENGTH_SHORT).show()
                isValid = false
            } else if (calculatedAge > 120) {
                Toast.makeText(requireContext(), "Please enter a valid birth year", Toast.LENGTH_SHORT).show()
                isValid = false
            }
        }
        
        // Terms checkbox validation
        if (!binding.termsCheckbox.isChecked) {
            Toast.makeText(requireContext(), "Please accept the Terms of Service", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        
        return isValid
    }
    
    /**
     * Perform sign up operation
     */
    private fun performSignUp() {
        try {
            if (!validateInput()) {
                return
            }
            
            val name = binding.editFullName.text.toString().trim()
            val email = binding.editEmail.text.toString().trim()
            val password = binding.editPassword.text.toString()
            
            // Check if email already exists (simple check in SharedPreferences)
            val existingEmail = prefsHelper.getString("registered_email", "")
            if (existingEmail.isNotEmpty() && existingEmail == email) {
                Toast.makeText(requireContext(), "Email already registered. Please sign in instead.", Toast.LENGTH_LONG).show()
                return
            }
            
            // Calculate age from birthday if provided
            val age = selectedBirthday?.let { 
                val calendar = Calendar.getInstance()
                calendar.time = it
                val birthYear = calendar.get(Calendar.YEAR)
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                currentYear - birthYear
            } ?: 25 // Default age
            
            // Create user profile with all information
            val userProfile = UserProfile(
                name = name,
                age = age,
                email = email,
                birthday = selectedBirthday?.let { dateFormat.format(it) } ?: "",
                gender = selectedGender,
                profileImageUri = "" // Will be set later when user uploads image
            )
            
            // Save user profile
            prefsHelper.saveUserProfile(userProfile)
            
            // Save account credentials
            prefsHelper.saveString("registered_email", email)
            prefsHelper.saveString("registered_password", password) // In real app, this should be hashed
            prefsHelper.saveString("user_name", name)
            
            // Mark as registered
            prefsHelper.saveBoolean("is_registered", true)
            
            Toast.makeText(requireContext(), "Account created successfully! 🎉 Please sign in to continue.", Toast.LENGTH_LONG).show()
            
            // Navigate to sign in
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to create account. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}