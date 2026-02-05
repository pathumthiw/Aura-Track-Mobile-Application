package com.nutriwise.auratracks.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.nutriwise.auratracks.R
import com.nutriwise.auratracks.data.SharedPreferencesHelper
import com.nutriwise.auratracks.databinding.FragmentSettingsBinding
import com.nutriwise.auratracks.sensors.ShakeDetector
import androidx.navigation.fragment.findNavController

/**
 * Fragment for app settings and preferences
 * Includes shake detection toggle and data export functionality
 */
class SettingsFragment : Fragment() {
    
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefsHelper: SharedPreferencesHelper
    private var shakeDetector: ShakeDetector? = null
    private var isShakeDetectionEnabled = false
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        prefsHelper = SharedPreferencesHelper(requireContext())
        setupClickListeners()
        loadSettings()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        shakeDetector?.stop()
        _binding = null
    }
    
    /**
     * Set up click listeners for UI elements
     */
    private fun setupClickListeners() {
        try {
            // Edit profile button
            binding.buttonEditProfile.setOnClickListener {
                try {
                    findNavController().navigate(R.id.userProfileFragment)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            // Hydration reminders toggle
            binding.switchHydrationReminders.setOnCheckedChangeListener { _, isChecked ->
                try {
                    prefsHelper.saveBoolean("hydration_reminders_enabled", isChecked)
                    Toast.makeText(requireContext(), 
                        if (isChecked) "Hydration reminders enabled" else "Hydration reminders disabled", 
                        Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            // Habit reminders toggle
            binding.switchHabitReminders.setOnCheckedChangeListener { _, isChecked ->
                try {
                    prefsHelper.saveBoolean("habit_reminders_enabled", isChecked)
                    Toast.makeText(requireContext(), 
                        if (isChecked) "Habit reminders enabled" else "Habit reminders disabled", 
                        Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            // Shake detection toggle
            binding.switchShakeDetection.setOnCheckedChangeListener { _, isChecked ->
                toggleShakeDetection(isChecked)
            }
            
            // Dark mode toggle
            binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
                try {
                    prefsHelper.saveBoolean("dark_mode_enabled", isChecked)
                    Toast.makeText(requireContext(), 
                        if (isChecked) "Dark mode enabled" else "Dark mode disabled", 
                        Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            // Color picker button
            binding.buttonColorPicker.setOnClickListener {
                try {
                    showColorPickerDialog()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            // Export data button
            binding.buttonExportData.setOnClickListener {
                exportData()
            }
            
            // Clear data button
            binding.buttonClearData.setOnClickListener {
                showClearDataConfirmation()
            }
            
            // About button
            binding.buttonAbout.setOnClickListener {
                showAboutDialog()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Load settings from SharedPreferences
     */
    private fun loadSettings() {
        try {
            // Load shake detection setting
            isShakeDetectionEnabled = prefsHelper.getBoolean("shake_detection_enabled", false)
            binding.switchShakeDetection.isChecked = isShakeDetectionEnabled
            
            // Load notification settings
            binding.switchHydrationReminders.isChecked = prefsHelper.getBoolean("hydration_reminders_enabled", true)
            binding.switchHabitReminders.isChecked = prefsHelper.getBoolean("habit_reminders_enabled", true)
            
            // Load appearance settings
            binding.switchDarkMode.isChecked = prefsHelper.getBoolean("dark_mode_enabled", false)
            
            if (isShakeDetectionEnabled) {
                startShakeDetection()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Toggle shake detection
     */
    private fun toggleShakeDetection(enabled: Boolean) {
        isShakeDetectionEnabled = enabled
        prefsHelper.saveBoolean("shake_detection_enabled", enabled)
        
        if (enabled) {
            startShakeDetection()
            Toast.makeText(requireContext(), "Shake detection enabled! Shake your device to quickly add a mood entry.", Toast.LENGTH_LONG).show()
        } else {
            stopShakeDetection()
            Toast.makeText(requireContext(), "Shake detection disabled", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Start shake detection
     */
    private fun startShakeDetection() {
        try {
            if (shakeDetector == null) {
                shakeDetector = ShakeDetector(requireContext()) {
                    // Shake detected - show quick mood dialog
                    showQuickMoodDialog()
                }
            }
            shakeDetector?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Stop shake detection
     */
    private fun stopShakeDetection() {
        try {
            shakeDetector?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Show quick mood dialog when shake is detected
     */
    private fun showQuickMoodDialog() {
        try {
            if (!isAdded || context == null) return
            
            val moods = arrayOf("😊 Happy", "😢 Sad", "😠 Angry", "🤩 Excited", "😌 Calm", "😴 Tired", "😰 Stressed", "😐 Neutral")
            val emojis = arrayOf("😊", "😢", "😠", "🤩", "😌", "😴", "😰", "😐")
            
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Quick Mood Entry")
                .setMessage("Shake detected! How are you feeling?")
                .setItems(moods) { _, which ->
                    try {
                        val selectedEmoji = emojis[which]
                        val now = java.util.Date()
                        
                        val moodEntry = com.nutriwise.auratracks.models.MoodEntry(
                            id = java.util.UUID.randomUUID().toString(),
                            emoji = selectedEmoji,
                            note = "Quick entry via shake detection",
                            date = now,
                            time = now
                        )
                        
                        prefsHelper.addMoodEntry(moodEntry)
                        Toast.makeText(requireContext(), "Mood saved: $selectedEmoji", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Export app data
     */
    private fun exportData() {
        try {
            val exportData = prefsHelper.exportData()
            
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "AuraTracks Data Export\n\n$exportData")
                putExtra(Intent.EXTRA_SUBJECT, "AuraTracks Data Export")
            }
            
            startActivity(Intent.createChooser(shareIntent, "Export Data"))
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error exporting data: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    /**
     * Show confirmation dialog for clearing data
     */
    private fun showClearDataConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Clear All Data")
            .setMessage("Are you sure you want to clear all data? This action cannot be undone and will delete all habits, mood entries, and hydration data.")
            .setPositiveButton("Clear") { _, _ ->
                clearAllData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    /**
     * Clear all app data
     */
    private fun clearAllData() {
        prefsHelper.clearAllData()
        Toast.makeText(requireContext(), "All data cleared successfully", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Show color picker dialog
     */
    private fun showColorPickerDialog() {
        try {
            if (!isAdded || context == null) return
            
            val colors = arrayOf("Blue", "Green", "Purple", "Orange", "Red", "Pink")
            val colorValues = arrayOf("#2196F3", "#4CAF50", "#9C27B0", "#FF9800", "#F44336", "#E91E63")
            
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Choose Accent Color")
                .setMessage("Select your favorite color theme")
                .setItems(colors) { _, which ->
                    try {
                        val selectedColor = colorValues[which]
                        prefsHelper.saveString("accent_color", selectedColor)
                        Toast.makeText(requireContext(), "Color theme updated!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Show about dialog
     */
    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("About AuraTracks")
            .setMessage("AuraTracks v1.0\n\nYour personal wellness companion for tracking daily habits, mood, and hydration.\n\nFeatures:\n• Daily habit tracking\n• Mood journaling with emoji\n• Hydration reminders\n• Shake detection for quick mood entries\n• Data export and backup")
            .setPositiveButton("OK", null)
            .show()
    }
}
