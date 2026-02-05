package com.nutriwise.auratracks.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.nutriwise.auratracks.R
import com.nutriwise.auratracks.adapters.NotificationsAdapter
import com.nutriwise.auratracks.data.NotificationManager
import com.nutriwise.auratracks.data.SharedPreferencesHelper
import com.nutriwise.auratracks.databinding.FragmentNotificationsBinding
import com.nutriwise.auratracks.models.Notification
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment for displaying notifications
 * Shows list of app notifications with mark as read functionality
 */
class NotificationsFragment : Fragment() {
    
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefsHelper: SharedPreferencesHelper
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationsAdapter: NotificationsAdapter
    private val notifications = mutableListOf<Notification>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            prefsHelper = SharedPreferencesHelper(requireContext())
            notificationManager = NotificationManager(requireContext())
            setupUI()
            setupRecyclerView()
            setupClickListeners()
            loadNotifications()
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
     * Setup RecyclerView
     */
    private fun setupRecyclerView() {
        try {
            notificationsAdapter = NotificationsAdapter(
                notifications = notifications,
                onNotificationClick = { notification ->
                    markAsRead(notification)
                },
                onNotificationAction = { notification, action ->
                    handleNotificationAction(notification, action)
                }
            )
            
            binding.notificationsRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = notificationsAdapter
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
            // Quick action cards
            binding.cardMarkAllRead.setOnClickListener {
                markAllAsRead()
            }
            
            binding.cardClearAll.setOnClickListener {
                clearAllNotifications()
            }
            
            binding.cardAddReminder.setOnClickListener {
                showAddReminderDialog()
            }
            
            binding.cardNotificationSettings.setOnClickListener {
                findNavController().navigate(R.id.settingsFragment)
            }
            
            // Reminder switches
            binding.switchHydrationReminder.setOnCheckedChangeListener { _, isChecked ->
                try {
                    prefsHelper.saveBoolean("hydration_reminders_enabled", isChecked)
                    Toast.makeText(requireContext(), 
                        if (isChecked) "Hydration reminders enabled" else "Hydration reminders disabled", 
                        Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            binding.switchHabitReminder.setOnCheckedChangeListener { _, isChecked ->
                try {
                    prefsHelper.saveBoolean("habit_reminders_enabled", isChecked)
                    Toast.makeText(requireContext(), 
                        if (isChecked) "Habit reminders enabled" else "Habit reminders disabled", 
                        Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            binding.switchMoodReminder.setOnCheckedChangeListener { _, isChecked ->
                try {
                    prefsHelper.saveBoolean("mood_reminders_enabled", isChecked)
                    Toast.makeText(requireContext(), 
                        if (isChecked) "Mood reminders enabled" else "Mood reminders disabled", 
                        Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Load notifications from storage
     */
    private fun loadNotifications() {
        try {
            notifications.clear()
            notifications.addAll(notificationManager.getAllNotifications())
            notificationsAdapter.notifyDataSetChanged()
            updateEmptyState()
            updateNotificationStats()
            loadReminderSettings()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Load reminder settings
     */
    private fun loadReminderSettings() {
        try {
            binding.switchHydrationReminder.isChecked = prefsHelper.getBoolean("hydration_reminders_enabled", true)
            binding.switchHabitReminder.isChecked = prefsHelper.getBoolean("habit_reminders_enabled", true)
            binding.switchMoodReminder.isChecked = prefsHelper.getBoolean("mood_reminders_enabled", true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Update notification statistics
     */
    private fun updateNotificationStats() {
        try {
            val totalCount = notifications.size
            val unreadCount = notifications.count { !it.isRead }
            val activeReminders = countActiveReminders()
            
            binding.totalNotificationsCount.text = totalCount.toString()
            binding.unreadNotificationsCount.text = unreadCount.toString()
            binding.activeRemindersCount.text = activeReminders.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Count active reminders
     */
    private fun countActiveReminders(): Int {
        var count = 0
        if (prefsHelper.getBoolean("hydration_reminders_enabled", true)) count++
        if (prefsHelper.getBoolean("habit_reminders_enabled", true)) count++
        if (prefsHelper.getBoolean("mood_reminders_enabled", true)) count++
        return count
    }
    
    /**
     * Mark notification as read
     */
    private fun markAsRead(notification: Notification) {
        try {
            if (!notification.isRead) {
                notificationManager.markAsRead(notification.id)
                loadNotifications()
                Toast.makeText(requireContext(), "Marked as read", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Mark all notifications as read
     */
    private fun markAllAsRead() {
        try {
            notificationManager.markAllAsRead()
            loadNotifications()
            Toast.makeText(requireContext(), "All notifications marked as read ✅", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Clear all notifications
     */
    private fun clearAllNotifications() {
        try {
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.setTitle("Clear All Notifications")
            builder.setMessage("Are you sure you want to clear all notifications? This action cannot be undone.")
            builder.setIcon(R.drawable.ic_notifications)
            
            builder.setPositiveButton("Clear All") { _, _ ->
                notificationManager.clearAllNotifications()
                loadNotifications()
                Toast.makeText(requireContext(), "All notifications cleared", Toast.LENGTH_SHORT).show()
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
     * Handle notification action
     */
    private fun handleNotificationAction(notification: Notification, action: String) {
        try {
            when (action) {
                "view_habits" -> {
                    findNavController().navigate(R.id.habitsFragment)
                }
                "view_mood" -> {
                    findNavController().navigate(R.id.moodFragment)
                }
                "view_hydration" -> {
                    findNavController().navigate(R.id.hydrationFragment)
                }
                "dismiss" -> {
                    notificationManager.removeNotification(notification.id)
                    loadNotifications()
                    Toast.makeText(requireContext(), "Notification dismissed", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Show add reminder dialog
     */
    private fun showAddReminderDialog() {
        try {
            if (!isAdded || context == null) return
            
            val reminderTypes = arrayOf("Hydration Reminder", "Habit Check-in", "Mood Check", "Custom Reminder")
            
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add New Reminder")
                .setMessage("Choose the type of reminder you'd like to add")
                .setItems(reminderTypes) { _, which ->
                    try {
                        when (which) {
                            0 -> {
                                prefsHelper.saveBoolean("hydration_reminders_enabled", true)
                                binding.switchHydrationReminder.isChecked = true
                                Toast.makeText(requireContext(), "Hydration reminder added!", Toast.LENGTH_SHORT).show()
                            }
                            1 -> {
                                prefsHelper.saveBoolean("habit_reminders_enabled", true)
                                binding.switchHabitReminder.isChecked = true
                                Toast.makeText(requireContext(), "Habit reminder added!", Toast.LENGTH_SHORT).show()
                            }
                            2 -> {
                                prefsHelper.saveBoolean("mood_reminders_enabled", true)
                                binding.switchMoodReminder.isChecked = true
                                Toast.makeText(requireContext(), "Mood reminder added!", Toast.LENGTH_SHORT).show()
                            }
                            3 -> {
                                Toast.makeText(requireContext(), "Custom reminders coming soon!", Toast.LENGTH_SHORT).show()
                            }
                        }
                        updateNotificationStats()
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
     * Update empty state
     */
    private fun updateEmptyState() {
        try {
            if (_binding != null) {
                if (notifications.isEmpty()) {
                    binding.emptyStateLayout.visibility = View.VISIBLE
                    binding.notificationsRecycler.visibility = View.GONE
                } else {
                    binding.emptyStateLayout.visibility = View.GONE
                    binding.notificationsRecycler.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
}
