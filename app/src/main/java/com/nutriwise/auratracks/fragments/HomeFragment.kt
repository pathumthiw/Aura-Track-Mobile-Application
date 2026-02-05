package com.nutriwise.auratracks.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutriwise.auratracks.R
import com.nutriwise.auratracks.adapters.RecentHabitsAdapter
import com.nutriwise.auratracks.adapters.RecentMoodsAdapter
import com.nutriwise.auratracks.data.SharedPreferencesHelper
import com.nutriwise.auratracks.data.NotificationManager
import com.nutriwise.auratracks.databinding.FragmentHomeBinding
import com.nutriwise.auratracks.models.TrendDirection
import com.nutriwise.auratracks.utils.HabitChartManager
import com.nutriwise.auratracks.utils.DemoDataGenerator
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null. Fragment may not be attached to view.")
    
    private lateinit var prefsHelper: SharedPreferencesHelper
    private lateinit var notificationManager: NotificationManager
    private lateinit var chartManager: HabitChartManager
    private lateinit var recentHabitsAdapter: RecentHabitsAdapter
    private lateinit var recentMoodsAdapter: RecentMoodsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            if (!isAdded || context == null) return
            
            prefsHelper = SharedPreferencesHelper(requireContext())
            notificationManager = NotificationManager(requireContext())
            chartManager = HabitChartManager(prefsHelper)
            setupRecyclerViews()
            setupClickListeners()
            updateDashboard()
            updateNotificationBadge()
            updateAnalyticsCard()
            

            if (prefsHelper.isNewUser()) {
                showNewUserExperience()
            } else {

                if (prefsHelper.shouldShowNewUserHints()) {
                    showNewUserHints()
                }
                updateDashboard()
            }
            
            // Clear demo data only once to avoid removing real user habits
            if (prefsHelper.getBoolean("demo_data_cleared", false).not()) {
                prefsHelper.clearDemoData()
                prefsHelper.saveBoolean("demo_data_cleared", true)
            }
            

            if (isFirstLaunch()) {
                notificationManager.createWelcomeNotifications()
                markFirstLaunchComplete()
            }
        } catch (e: Exception) {
            e.printStackTrace()

            if (_binding != null && isAdded) {
                try {
                    binding.welcomeText.text = "Welcome to AuraTracks!"
                } catch (bindingError: Exception) {
                    bindingError.printStackTrace()
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        try {
            if (!isAdded || context == null || _binding == null) return
            

            updateDashboard()
            updateRecentActivity()
            updateNotificationBadge()
            updateAnalyticsCard()
            

            checkForMoodDataUpdates()
            checkForHabitDataUpdates()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    

    private fun setupRecyclerViews() {
        try {
            if (!isAdded || context == null || _binding == null) return
            

            recentHabitsAdapter = RecentHabitsAdapter(emptyList(), emptyList())
            binding.recentHabitsRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = recentHabitsAdapter
            }
            

            recentMoodsAdapter = RecentMoodsAdapter(emptyList())
            binding.recentMoodsRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = recentMoodsAdapter
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    private fun setupClickListeners() {
        try {
            // Quick action cards
            binding.cardAddHabit.setOnClickListener {
                try {
                    findNavController().navigate(R.id.habitsFragment)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            binding.cardLogMood.setOnClickListener {
                try {
                    findNavController().navigate(R.id.moodFragment)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            

            binding.buttonViewAllHabits.setOnClickListener {
                try {
                    findNavController().navigate(R.id.habitsFragment)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            binding.buttonViewAllMoods.setOnClickListener {
                try {
                    findNavController().navigate(R.id.moodFragment)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            

            binding.notificationBellContainer.setOnClickListener {
                try {
                    findNavController().navigate(R.id.notificationsFragment)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            

            binding.cardHydration.setOnClickListener {
                try {
                    findNavController().navigate(R.id.hydrationFragment)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            

            binding.cardNotifications.setOnClickListener {
                try {
                    findNavController().navigate(R.id.notificationsFragment)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            // Analytics card click listener
            binding.cardHabitAnalytics.setOnClickListener {
                try {
                    findNavController().navigate(R.id.habitAnalyticsFragment)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    private fun updateDashboard() {
        try {
            if (!isAdded || context == null || _binding == null) return
            
            updateWelcomeMessage()
            updateQuickStats()
            updateRecentActivity()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    private fun updateWelcomeMessage() {
        try {
            if (!isAdded || context == null || _binding == null) return
            
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            
            val (greeting, emoji) = when (hour) {
                in 5..11 -> "Good Morning!" to "🌅"
                in 12..17 -> "Good Afternoon!" to "☀️"
                in 18..21 -> "Good Evening!" to "🌆"
                else -> "Good Night!" to "🌙"
            }
            
            binding.welcomeText.text = greeting
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    private fun updateQuickStats() {
        try {
            if (!isAdded || context == null || _binding == null) return
            

            val habits = prefsHelper.getHabits().filter { it.isActive }
            val completedToday = prefsHelper.getHabitCompletions()
                .count { it.isCompleted && it.date == getCurrentDateString() }
            
            binding.habitsProgressText.text = "$completedToday/${habits.size}"
            

            val hydrationData = prefsHelper.getTodayHydrationData()
            binding.waterIntakeText.text = "${hydrationData.glassesDrank}/${hydrationData.dailyGoal}"
        } catch (e: Exception) {
            e.printStackTrace()

            if (!isAdded || context == null || _binding == null) return
            
            try {
                binding.habitsProgressText.text = "0/0"
                binding.waterIntakeText.text = "0/8"
            } catch (bindingError: Exception) {
                bindingError.printStackTrace()
            }
        }
    }
    

    private fun updateRecentActivity() {
        try {
            if (!isAdded || context == null || _binding == null) return
            

            val today = getCurrentDateString()
            val recentCompletions = prefsHelper.getHabitCompletions()
                .filter { it.date == today && it.isCompleted }
                .sortedByDescending { it.completedAt }
                .take(3)
            
            val recentHabits = recentCompletions.mapNotNull { completion ->
                prefsHelper.getHabits().find { it.id == completion.habitId }
            }
            
            recentHabitsAdapter.updateHabits(recentHabits, recentCompletions)
            

            val recentMoods = prefsHelper.getMoodEntries()
                .sortedByDescending { it.date }
                .take(3)
            
            recentMoodsAdapter.updateMoods(recentMoods)
        } catch (e: Exception) {
            e.printStackTrace()

            try {
                if (::recentHabitsAdapter.isInitialized && ::recentMoodsAdapter.isInitialized) {
                    recentHabitsAdapter.updateHabits(emptyList(), emptyList())
                    recentMoodsAdapter.updateMoods(emptyList())
                }
            } catch (adapterError: Exception) {
                adapterError.printStackTrace()
            }
        }
    }
    

    private fun getCurrentDateString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
    

    private fun updateNotificationBadge() {
        try {
            if (!isAdded || context == null || _binding == null) return
            
            val unreadCount = notificationManager.getUnreadCount()
            
            if (unreadCount > 0) {
                binding.notificationBadge.visibility = android.view.View.VISIBLE
                binding.notificationBadge.text = when {
                    unreadCount > 99 -> "99+"
                    else -> unreadCount.toString()
                }
            } else {
                binding.notificationBadge.visibility = android.view.View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    private fun isFirstLaunch(): Boolean {
        return !prefsHelper.getBoolean("first_launch_complete", false)
    }
    

    private fun markFirstLaunchComplete() {
        prefsHelper.saveBoolean("first_launch_complete", true)
    }
    

    private fun showNewUserExperience() {
        try {
            if (!isAdded || context == null || _binding == null) return
            
            // Show welcome message
            binding.welcomeText.text = "Welcome! Let's start your wellness journey! 🌟"
            
            // Hide dynamic content sections and show empty states
            binding.recentHabitsRecycler.visibility = android.view.View.GONE
            binding.recentMoodsRecycler.visibility = android.view.View.GONE
            
            // Show new user guidance cards
            showEmptyStatesWithGuidance()
            
            // Set up sample data suggestions
            showSetupSuggestions()
            
            Toast.makeText(requireContext(), "Welcome to AuraTracks! Start by exploring the features below.", Toast.LENGTH_LONG).show()
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    private fun showNewUserHints() {
        try {
            if (!isAdded || context == null || _binding == null) return
            
            val daysSinceLaunch = prefsHelper.getDaysSinceFirstLaunch()
            

            val hintMessage = when (daysSinceLaunch) {
                0 -> "Welcome! Try adding your first habit today!"
                1 -> "Great start! How about logging your mood?"
                2 -> "Keep it up! Don't forget to hydrate 💧"
                3 -> "You're building momentum! Check your progress"
                4 -> "Nice consistency! Consider adding more habits"
                5 -> "Almost week one! Review your achievements"
                6 -> "Last day of week one! Set next week's goals"
                else -> "Great progress! Keep building healthy habits"
            }
            
            binding.welcomeText.text = hintMessage
            

            if (!prefsHelper.getBoolean("new_user_tip_shown_$daysSinceLaunch", false)) {
                binding.notificationBadge.visibility = android.view.View.VISIBLE
                prefsHelper.saveBoolean("new_user_tip_shown_$daysSinceLaunch", true)
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    private fun showEmptyStatesWithGuidance() {
        try {
            if (!isAdded || context == null || _binding == null) return
            
            // Clear existing adapters with empty data
            if (::recentHabitsAdapter.isInitialized && ::recentMoodsAdapter.isInitialized) {
                recentHabitsAdapter.updateHabits(emptyList(), emptyList())
                recentMoodsAdapter.updateMoods(emptyList())
            }
            

            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    private fun showSetupSuggestions() {
        try {
            val suggestions = listOf(
                "💡 Tip: Start with just 1-2 simple habits",
                "💡 Tip: Log your mood daily for better insights", 
                "💡 Tip: Set a water goal to stay hydrated",
                "💡 Tip: Review your progress weekly"
            )
            

            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    private fun completeNewUserHints() {
        try {
            if (!isAdded || context == null || _binding == null) return
            
            prefsHelper.completeNewUserHints()
            binding.notificationBadge.visibility = android.view.View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    private fun checkForMoodDataUpdates() {
        try {
            if (!isAdded || context == null || _binding == null) return
            
            val hasDataChanged = prefsHelper.getBoolean("mood_data_changed", false)
            if (hasDataChanged) {

                prefsHelper.saveBoolean("mood_data_changed", false)
                

                updateRecentActivity()
                
                
                Toast.makeText(requireContext(), "✨ Home screen updated with latest mood!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Check for habit data updates and refresh analytics
     */
    private fun checkForHabitDataUpdates() {
        try {
            if (!isAdded || context == null || _binding == null) return
            
            val hasDataChanged = prefsHelper.getBoolean("habits_data_changed", false)
            if (hasDataChanged) {
                // Reset the flag
                prefsHelper.saveBoolean("habits_data_changed", false)
                
                // Update analytics and dashboard
                updateAnalyticsCard()
                updateDashboard()
                
                // Show success message
                Toast.makeText(requireContext(), "✨ Analytics updated with latest habits!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Update the analytics card with current habit performance data
     */
    private fun updateAnalyticsCard() {
        try {
            if (!isAdded || context == null || _binding == null) return
            
            val overallAnalytics = chartManager.generateOverallAnalytics()
            
            // Update completion rate
            binding.tvAnalyticsCompletionRate.text = "${overallAnalytics.averageCompletionRate.toInt()}%"
            
            // Update best performing habit
            val bestHabit = overallAnalytics.bestPerformingHabit
            binding.tvAnalyticsBestHabit.text = bestHabit ?: "No data"
            
            // Update trend indicator
            val trendEmoji = when (overallAnalytics.monthlyTrend) {
                TrendDirection.IMPROVING -> "📈"
                TrendDirection.DECLINING -> "📉"
                TrendDirection.STABLE -> "➡️"
                TrendDirection.NO_DATA -> "📊"
            }
            binding.tvAnalyticsTrend.text = trendEmoji
            
            // Update summary text
            val summaryText = when {
                overallAnalytics.totalHabits == 0 -> "Add your first habit to see analytics"
                overallAnalytics.averageCompletionRate >= 80 -> "Great job! You're doing amazing"
                overallAnalytics.averageCompletionRate >= 60 -> "Good progress! Keep it up"
                overallAnalytics.averageCompletionRate >= 40 -> "You're building momentum"
                else -> "Every step counts! Keep going"
            }
            binding.tvAnalyticsSummary.text = summaryText
            
        } catch (e: Exception) {
            e.printStackTrace()
            
            // Set default values on error
            try {
                binding.tvAnalyticsCompletionRate.text = "0%"
                binding.tvAnalyticsBestHabit.text = "--"
                binding.tvAnalyticsTrend.text = "📊"
                binding.tvAnalyticsSummary.text = "View your progress and insights"
            } catch (bindingError: Exception) {
                bindingError.printStackTrace()
            }
        }
    }
}
