package com.nutriwise.auratracks.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.nutriwise.auratracks.R
import com.nutriwise.auratracks.data.SharedPreferencesHelper
import com.nutriwise.auratracks.databinding.FragmentHydrationBinding
import com.nutriwise.auratracks.services.HydrationReminderService

/**
 * Fragment for hydration tracking and reminders
 * Allows users to track water intake and set up reminders
 */
class HydrationFragment : Fragment() {
    
    private var _binding: FragmentHydrationBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefsHelper: SharedPreferencesHelper
    private var currentGlasses = 0
    private var dailyGoal = 8
    private var reminderInterval = 60
    
    // Glass cards for interactive water intake
    private val glassCards = mutableListOf<MaterialCardView>()
    
    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHydrationBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            prefsHelper = SharedPreferencesHelper(requireContext())
            loadHydrationData()
            setupClickListeners()
            updateUI()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    /**
     * Load hydration data from SharedPreferences
     */
    private fun loadHydrationData() {
        try {
            val hydrationData = prefsHelper.getTodayHydrationData()
            currentGlasses = hydrationData.glassesDrank
            dailyGoal = hydrationData.dailyGoal
            
            // Load reminder settings
            val reminderSettings = prefsHelper.getHydrationSettings()
            reminderInterval = reminderSettings.intervalMinutes
        } catch (e: Exception) {
            e.printStackTrace()
            // Set default values if there's an error
            currentGlasses = 0
            dailyGoal = 8
            reminderInterval = 60
        }
    }
    
    /**
     * Set up click listeners for UI elements
     */
    private fun setupClickListeners() {
        try {
            // Add glass button
            binding.buttonAddGlass.setOnClickListener {
                try {
                    addGlass()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            // Remove glass button
            binding.buttonRemoveGlass.setOnClickListener {
                try {
                    removeGlass()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            // Setup interactive glass cards
            setupGlassCards()
            
            // Daily goal slider
            binding.sliderDailyGoal.addOnChangeListener { _, value, _ ->
                try {
                    dailyGoal = value.toInt()
                    prefsHelper.setDailyGoal(dailyGoal)
                    updateUI()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            // Reminder switch
            binding.switchReminders.setOnCheckedChangeListener { _, isChecked ->
                try {
                    if (isChecked) {
                        requestNotificationPermission()
                    } else {
                        stopReminders()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            // Reminder interval slider
            binding.sliderReminderInterval.addOnChangeListener { _, value, _ ->
                try {
                    reminderInterval = value.toInt()
                    val settings = prefsHelper.getHydrationSettings().copy(
                        intervalMinutes = reminderInterval
                    )
                    prefsHelper.saveHydrationSettings(settings)
                    updateReminderIntervalText()
                    
                    // Restart reminders if they're enabled
                    if (binding.switchReminders.isChecked) {
                        startReminders()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Setup interactive glass cards
     */
    private fun setupGlassCards() {
        try {
            // Initialize glass cards array
            glassCards.clear()
            glassCards.addAll(listOf(
                binding.glass1,
                binding.glass2,
                binding.glass3,
                binding.glass4,
                binding.glass5,
                binding.glass6,
                binding.glass7,
                binding.glass8
            ))
            
            // Set click listeners for each glass card
            glassCards.forEachIndexed { index, card ->
                card.setOnClickListener {
                    try {
                        val targetGlasses = index + 1
                        if (targetGlasses > currentGlasses) {
                            // Add glasses up to the clicked glass
                            while (currentGlasses < targetGlasses) {
                                addGlass()
                            }
                        } else if (targetGlasses < currentGlasses) {
                            // Remove glasses down to the clicked glass
                            while (currentGlasses > targetGlasses) {
                                removeGlass()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Update UI elements
     */
    private fun updateUI() {
        try {
            if (_binding != null) {
                // Update glasses count
                binding.glassesCount.text = currentGlasses.toString()
                
                // Update progress text
                binding.progressText.text = "$currentGlasses / $dailyGoal glasses"
                
                // Update linear progress bar
                val progress = if (dailyGoal > 0) {
                    currentGlasses.toFloat() / dailyGoal.toFloat()
                } else {
                    0f
                }
                binding.progressLinear.setProgressCompat((progress * 100).toInt(), true)
                
                // Update daily goal text
                binding.dailyGoalText.text = "$dailyGoal glasses"
                
                // Update sliders
                binding.sliderDailyGoal.value = dailyGoal.toFloat()
                binding.sliderReminderInterval.value = reminderInterval.toFloat()
                
                // Update reminder switch
                binding.switchReminders.isChecked = prefsHelper.isReminderEnabled()
                
                // Update glass cards visualization
                updateGlassCards()
                
                updateReminderIntervalText()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Update reminder interval text
     */
    private fun updateReminderIntervalText() {
        try {
            if (_binding != null) {
                val hours = reminderInterval / 60
                val minutes = reminderInterval % 60
                
                val text = when {
                    hours > 0 && minutes > 0 -> "$hours h $minutes min"
                    hours > 0 -> "$hours hour${if (hours > 1) "s" else ""}"
                    else -> "$minutes minutes"
                }
                
                binding.reminderIntervalText.text = text
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Update glass cards visualization
     */
    private fun updateGlassCards() {
        try {
            glassCards.forEachIndexed { index, card ->
                if (index < currentGlasses) {
                    // Glass is filled
                    card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.water_blue))
                    card.alpha = 1.0f
                } else {
                    // Glass is empty
                    card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.water_blue_light))
                    card.alpha = 0.3f
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Add a glass of water
     */
    private fun addGlass() {
        try {
            prefsHelper.addGlassOfWater()
            currentGlasses++
            updateUI()
            
            // Show celebration if goal reached
            if (currentGlasses >= dailyGoal) {
                Toast.makeText(requireContext(), "🎉 Daily goal reached! Great job!", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Remove a glass of water
     */
    private fun removeGlass() {
        try {
            if (currentGlasses > 0) {
                prefsHelper.removeGlassOfWater()
                currentGlasses--
                updateUI()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Request notification permission
     */
    private fun requestNotificationPermission() {
        try {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            } else {
                startReminders()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Start hydration reminders
     */
    private fun startReminders() {
        try {
            val settings = prefsHelper.getHydrationSettings().copy(
                isEnabled = true,
                intervalMinutes = reminderInterval
            )
            prefsHelper.saveHydrationSettings(settings)
            prefsHelper.setReminderEnabled(true)
            
            // Start the reminder service
            val intent = android.content.Intent(requireContext(), HydrationReminderService::class.java)
            requireContext().startService(intent)
            
            Toast.makeText(requireContext(), "Hydration reminders enabled", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Stop hydration reminders
     */
    private fun stopReminders() {
        try {
            val settings = prefsHelper.getHydrationSettings().copy(isEnabled = false)
            prefsHelper.saveHydrationSettings(settings)
            prefsHelper.setReminderEnabled(false)
            
            // Stop the reminder service
            val intent = android.content.Intent(requireContext(), HydrationReminderService::class.java)
            requireContext().stopService(intent)
            
            Toast.makeText(requireContext(), "Hydration reminders disabled", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        try {
            if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startReminders()
                } else {
                    if (_binding != null) {
                        binding.switchReminders.isChecked = false
                    }
                    Toast.makeText(
                        requireContext(),
                        "Notification permission is required for reminders",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
