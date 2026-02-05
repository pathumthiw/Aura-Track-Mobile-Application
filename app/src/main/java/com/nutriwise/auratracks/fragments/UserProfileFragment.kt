package com.nutriwise.auratracks.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nutriwise.auratracks.R
import com.nutriwise.auratracks.data.SharedPreferencesHelper
import com.nutriwise.auratracks.databinding.FragmentUserProfileBinding
import com.nutriwise.auratracks.models.UserProfile
import com.nutriwise.auratracks.utils.HabitChartManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * Modern user profile management fragment
 * Features: Profile completion tracking, statistics, modern UI, data export
 */
class UserProfileFragment : Fragment() {
    
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")
    
    private lateinit var prefsHelper: SharedPreferencesHelper
    private lateinit var chartManager: HabitChartManager
    private var selectedImageUri: Uri? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    // Image picker launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                try {
                    binding.profileImage.setImageURI(selectedImageUri)
                    updateProfileCompletion()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            prefsHelper = SharedPreferencesHelper(requireContext())
            chartManager = HabitChartManager(prefsHelper)
            
            setupUI()
            loadUserProfile()
            setupClickListeners()
            setupValidation()
            updateStatistics()
            updateProfileCompletion()
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onResume() {
        super.onResume()
        updateStatistics()
        updateProfileCompletion()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    /**
     * Setup UI components
     */
    private fun setupUI() {
        try {
            // Setup toolbar
            binding.toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Load existing user profile data
     */
    private fun loadUserProfile() {
        try {
            val profile = prefsHelper.getUserProfile()
            
            binding.editName.setText(profile.name)
            binding.editAge.setText(if (profile.age > 0) profile.age.toString() else "")
            binding.editEmail.setText(profile.email)
            
            if (profile.birthday.isNotEmpty()) {
                binding.editBirthday.setText(profile.birthday)
            }
            
            binding.editGender.setText(profile.gender)
            
            // Update profile display
            binding.tvProfileName.text = profile.name.ifEmpty { "Your Name" }
            binding.tvProfileEmail.text = profile.email.ifEmpty { "your.email@example.com" }
            
            // Load profile image if exists
            if (profile.profileImageUri.isNotEmpty()) {
                try {
                    val uri = Uri.parse(profile.profileImageUri)
                    binding.profileImage.setImageURI(uri)
                    selectedImageUri = uri
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
            // Change photo button
            binding.changePhotoButton.setOnClickListener {
                showImagePickerOptions()
            }
            
            // Birthday picker
            binding.editBirthday.setOnClickListener {
                showDatePicker()
            }
            
            // Gender selection
            binding.editGender.setOnClickListener {
                showGenderPicker()
            }
            
            // Save button
            binding.saveButton.setOnClickListener {
                saveProfile()
            }
            
            // Logout button
            binding.logoutButton.setOnClickListener {
                showLogoutDialog()
            }
            
            // Export data
            binding.exportDataContainer.setOnClickListener {
                exportUserData()
            }
            
            // Privacy settings
            binding.privacySettingsContainer.setOnClickListener {
                showPrivacySettings()
            }
            
            // Notification switch
            binding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
                prefsHelper.saveBoolean("notifications_enabled", isChecked)
                Toast.makeText(requireContext(), 
                    if (isChecked) "Notifications enabled" else "Notifications disabled", 
                    Toast.LENGTH_SHORT).show()
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
            // Name validation
            binding.editName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    validateName()
                    updateProfileCompletion()
                }
            })
            
            // Age validation
            binding.editAge.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    validateAge()
                    updateProfileCompletion()
                }
            })
            
            // Email validation
            binding.editEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    validateEmail()
                    updateProfileCompletion()
                }
            })
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Show image picker options (Camera or Gallery)
     */
    private fun showImagePickerOptions() {
        try {
            val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
            
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.setTitle("Select Profile Photo")
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openImagePicker()
                    2 -> { /* Cancel */ }
                }
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Open camera for taking photo
     */
    private fun openCamera() {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                imagePickerLauncher.launch(intent)
            } else {
                Toast.makeText(requireContext(), "Camera not available", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Unable to open camera", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Open image picker for gallery
     */
    private fun openImagePicker() {
        try {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Unable to open image picker", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Show date picker for birthday
     */
    private fun showDatePicker() {
        try {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_YEAR)
            
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(selectedYear, selectedMonth, selectedDay)
                    binding.editBirthday.setText(dateFormat.format(selectedDate.time))
                    updateProfileCompletion()
                },
                year, month, day
            )
            
            // Set max date to today
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Show gender picker
     */
    private fun showGenderPicker() {
        try {
            val genders = arrayOf("Male", "Female", "Other", "Prefer not to say")
            
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.setTitle("Select Gender")
            builder.setItems(genders) { _, which ->
                binding.editGender.setText(genders[which])
                updateProfileCompletion()
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Update profile statistics
     */
    private fun updateStatistics() {
        try {
            val overallAnalytics = chartManager.generateOverallAnalytics()
            
            binding.tvTotalHabits.text = overallAnalytics.totalHabits.toString()
            binding.tvTotalCompletions.text = overallAnalytics.totalCompletions.toString()
            
            // Calculate current streak (simplified)
            val habits = prefsHelper.getHabits()
            val completions = prefsHelper.getHabitCompletions()
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            
            val todayCompletions = completions.count { it.date == today && it.isCompleted }
            binding.tvStreakDays.text = if (todayCompletions > 0) "1" else "0"
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Update profile completion progress
     */
    private fun updateProfileCompletion() {
        try {
            var completionCount = 0
            val totalFields = 6 // name, email, age, birthday, gender, photo
            
            // Check each field
            if (binding.editName.text.toString().trim().isNotEmpty()) completionCount++
            if (binding.editEmail.text.toString().trim().isNotEmpty()) completionCount++
            if (binding.editAge.text.toString().trim().isNotEmpty()) completionCount++
            if (binding.editBirthday.text.toString().trim().isNotEmpty()) completionCount++
            if (binding.editGender.text.toString().trim().isNotEmpty()) completionCount++
            if (selectedImageUri != null) completionCount++
            
            val percentage = (completionCount * 100) / totalFields
            
            binding.tvCompletionPercentage.text = "$percentage%"
            binding.profileCompletionProgress.progress = percentage
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Validate name input
     */
    private fun validateName(): Boolean {
        val name = binding.editName.text.toString().trim()
        return when {
            name.isEmpty() -> {
                binding.nameInputLayout.error = "Name is required"
                false
            }
            name.length < 2 -> {
                binding.nameInputLayout.error = "Name must be at least 2 characters"
                false
            }
            !name.matches(Regex("^[a-zA-Z\\s]+$")) -> {
                binding.nameInputLayout.error = "Name can only contain letters and spaces"
                false
            }
            else -> {
                binding.nameInputLayout.error = null
                binding.tvProfileName.text = name
                true
            }
        }
    }
    
    /**
     * Validate age input
     */
    private fun validateAge(): Boolean {
        val ageText = binding.editAge.text.toString().trim()
        return when {
            ageText.isEmpty() -> {
                binding.ageInputLayout.error = null // Age is optional
                true
            }
            else -> {
                try {
                    val age = ageText.toInt()
                    when {
                        age < 1 -> {
                            binding.ageInputLayout.error = "Age must be at least 1"
                            false
                        }
                        age > 150 -> {
                            binding.ageInputLayout.error = "Age must be less than 150"
                            false
                        }
                        else -> {
                            binding.ageInputLayout.error = null
                            true
                        }
                    }
                } catch (e: NumberFormatException) {
                    binding.ageInputLayout.error = "Please enter a valid age"
                    false
                }
            }
        }
    }
    
    /**
     * Validate email input
     */
    private fun validateEmail(): Boolean {
        val email = binding.editEmail.text.toString().trim()
        return when {
            email.isEmpty() -> {
                binding.emailInputLayout.error = null // Email is optional
                binding.tvProfileEmail.text = "your.email@example.com"
                true
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.emailInputLayout.error = "Please enter a valid email address"
                false
            }
            else -> {
                binding.emailInputLayout.error = null
                binding.tvProfileEmail.text = email
                true
            }
        }
    }
    
    /**
     * Validate all inputs
     */
    private fun validateAllInputs(): Boolean {
        val isNameValid = validateName()
        val isAgeValid = validateAge()
        val isEmailValid = validateEmail()
        
        return isNameValid && isAgeValid && isEmailValid
    }
    
    /**
     * Save user profile
     */
    private fun saveProfile() {
        try {
            if (!validateAllInputs()) {
                Toast.makeText(requireContext(), "Please fix the errors above", Toast.LENGTH_SHORT).show()
                return
            }
            
            val name = binding.editName.text.toString().trim()
            val ageText = binding.editAge.text.toString().trim()
            val email = binding.editEmail.text.toString().trim()
            val birthday = binding.editBirthday.text.toString().trim()
            val gender = binding.editGender.text.toString().trim()
            
            val age = if (ageText.isNotEmpty()) {
                try {
                    ageText.toInt()
                } catch (e: NumberFormatException) {
                    0
                }
            } else {
                0
            }
            
            val profileImageUri = selectedImageUri?.toString() ?: ""
            
            val userProfile = UserProfile(
                name = name,
                age = age,
                email = email,
                birthday = birthday,
                gender = gender,
                profileImageUri = profileImageUri
            )
            
            prefsHelper.saveUserProfile(userProfile)
            
            Toast.makeText(requireContext(), "Profile saved successfully! ✅", Toast.LENGTH_SHORT).show()
            
            // Update display
            binding.tvProfileName.text = name.ifEmpty { "Your Name" }
            binding.tvProfileEmail.text = email.ifEmpty { "your.email@example.com" }
            
            // Navigate back
            findNavController().navigateUp()
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to save profile", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Export user data
     */
    private fun exportUserData() {
        try {
            val exportData = prefsHelper.exportData()
            
            // Create share intent
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "AuraTracks Data Export\n\n$exportData")
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "AuraTracks Data Backup")
            
            startActivity(Intent.createChooser(shareIntent, "Export Data"))
            
            Toast.makeText(requireContext(), "Data exported successfully! 📤", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to export data", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Show privacy settings
     */
    private fun showPrivacySettings() {
        try {
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.setTitle("Privacy Settings")
            builder.setMessage("Your data is stored locally on your device and is not shared with any third parties. You can export your data anytime for backup purposes.")
            
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Show logout confirmation dialog
     */
    private fun showLogoutDialog() {
        try {
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.setTitle("Sign Out")
            builder.setMessage("Are you sure you want to sign out? You will need to sign in again to access your data.")
            builder.setIcon(R.drawable.ic_logout)
            
            builder.setPositiveButton("Sign Out") { _, _ ->
                performLogout()
            }
            
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Perform logout operation
     */
    private fun performLogout() {
        try {
            // Clear login state
            prefsHelper.saveBoolean("is_logged_in", false)
            prefsHelper.saveString("user_email", "")
            prefsHelper.saveBoolean("is_guest", false)
            
            // Show logout message
            Toast.makeText(requireContext(), "You have been signed out successfully 👋", Toast.LENGTH_LONG).show()
            
            // Navigate to sign in page
            findNavController().navigate(R.id.action_userProfileFragment_to_signInFragment)
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Sign out failed", Toast.LENGTH_SHORT).show()
        }
    }
}