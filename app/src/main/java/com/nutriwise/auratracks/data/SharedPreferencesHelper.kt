package com.nutriwise.auratracks.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nutriwise.auratracks.models.*
import java.text.SimpleDateFormat
import java.util.*


class SharedPreferencesHelper(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    companion object {
        private const val PREFS_NAME = "AuraTracksPrefs"
        private const val KEY_HABITS = "habits"
        private const val KEY_HABIT_COMPLETIONS = "habit_completions"
        private const val KEY_MOOD_ENTRIES = "mood_entries"
        private const val KEY_HYDRATION_DATA = "hydration_data"
        private const val KEY_HYDRATION_SETTINGS = "hydration_settings"
        private const val KEY_DAILY_GOAL = "daily_goal"
        private const val KEY_REMINDER_ENABLED = "reminder_enabled"
        private const val KEY_REMINDER_INTERVAL = "reminder_interval"
    }
    

    fun saveHabits(habits: List<Habit>) {
        val habitsJson = gson.toJson(habits)
        prefs.edit().putString(KEY_HABITS, habitsJson).apply()
    }
    
    fun getHabits(): List<Habit> {
        return try {
            val habitsJson = prefs.getString(KEY_HABITS, null)
            if (habitsJson != null) {
                val type = object : TypeToken<List<Habit>>() {}.type
                gson.fromJson(habitsJson, type) ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    fun addHabit(habit: Habit) {
        val habits = getHabits().toMutableList()
        habits.add(habit)
        saveHabits(habits)
    }
    
    fun updateHabit(habit: Habit) {
        val habits = getHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            habits[index] = habit
            saveHabits(habits)
        }
    }
    
    fun deleteHabit(habitId: String) {
        val habits = getHabits().toMutableList()
        habits.removeAll { it.id == habitId }
        saveHabits(habits)
        

        val completions = getHabitCompletions().toMutableList()
        completions.removeAll { it.habitId == habitId }
        saveHabitCompletions(completions)
    }
    

    fun saveHabitCompletions(completions: List<HabitCompletion>) {
        val completionsJson = gson.toJson(completions)
        prefs.edit().putString(KEY_HABIT_COMPLETIONS, completionsJson).apply()
    }
    
    fun getHabitCompletions(): List<HabitCompletion> {
        return try {
            val completionsJson = prefs.getString(KEY_HABIT_COMPLETIONS, null)
            if (completionsJson != null) {
                val type = object : TypeToken<List<HabitCompletion>>() {}.type
                gson.fromJson(completionsJson, type) ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    fun toggleHabitCompletion(habitId: String, date: Date = Date()): Boolean {
        val dateString = dateFormat.format(date)
        val completions = getHabitCompletions().toMutableList()
        
        val existingCompletion = completions.find { it.habitId == habitId && it.date == dateString }
        
        return if (existingCompletion != null) {

            val newCompletion = existingCompletion.copy(
                isCompleted = !existingCompletion.isCompleted,
                completedAt = if (!existingCompletion.isCompleted) Date() else null
            )
            val index = completions.indexOf(existingCompletion)
            completions[index] = newCompletion
            saveHabitCompletions(completions)
            newCompletion.isCompleted
        } else {

            val newCompletion = HabitCompletion(
                habitId = habitId,
                date = dateString,
                isCompleted = true,
                completedAt = Date()
            )
            completions.add(newCompletion)
            saveHabitCompletions(completions)
            true
        }
    }
    
    fun getHabitCompletionForDate(habitId: String, date: Date = Date()): HabitCompletion? {
        val dateString = dateFormat.format(date)
        return getHabitCompletions().find { it.habitId == habitId && it.date == dateString }
    }
    
    fun getTodayProgress(): Float {
        val today = dateFormat.format(Date())
        val habits = getHabits().filter { it.isActive }
        if (habits.isEmpty()) return 0f
        
        val completions = getHabitCompletions()
        val completedToday = completions.count { 
            it.date == today && it.isCompleted && habits.any { habit -> habit.id == it.habitId }
        }
        
        return completedToday.toFloat() / habits.size
    }
    

    fun saveMoodEntries(entries: List<MoodEntry>) {
        val entriesJson = gson.toJson(entries)
        prefs.edit().putString(KEY_MOOD_ENTRIES, entriesJson).apply()
    }
    
    fun getMoodEntries(): List<MoodEntry> {
        return try {
            val entriesJson = prefs.getString(KEY_MOOD_ENTRIES, null)
            if (entriesJson != null) {
                val type = object : TypeToken<List<MoodEntry>>() {}.type
                gson.fromJson(entriesJson, type) ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    fun addMoodEntry(entry: MoodEntry) {
        val entries = getMoodEntries().toMutableList()
        entries.add(entry)
        saveMoodEntries(entries)
    }
    
    fun getMoodEntriesForDate(date: Date = Date()): List<MoodEntry> {
        val dateString = dateFormat.format(date)
        return getMoodEntries().filter { dateFormat.format(it.date) == dateString }
    }
    

    fun saveHydrationData(data: HydrationData) {
        val dataJson = gson.toJson(data)
        prefs.edit().putString(KEY_HYDRATION_DATA, dataJson).apply()
    }
    
    fun getHydrationData(): HydrationData? {
        val dataJson = prefs.getString(KEY_HYDRATION_DATA, null)
        return if (dataJson != null) {
            gson.fromJson(dataJson, HydrationData::class.java)
        } else {
            null
        }
    }
    
    fun getTodayHydrationData(): HydrationData {
        return try {
            val today = dateFormat.format(Date())
            val data = getHydrationData()
            
            if (data != null && data.date == today) {
                data
            } else {
                HydrationData(
                    date = today,
                    glassesDrank = 0,
                    dailyGoal = getDailyGoal(),
                    lastUpdated = Date()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()

            HydrationData(
                date = dateFormat.format(Date()),
                glassesDrank = 0,
                dailyGoal = 8,
                lastUpdated = Date()
            )
        }
    }
    
    fun addGlassOfWater() {
        val todayData = getTodayHydrationData()
        val updatedData = todayData.copy(
            glassesDrank = todayData.glassesDrank + 1,
            lastUpdated = Date()
        )
        saveHydrationData(updatedData)
    }
    
    fun removeGlassOfWater() {
        val todayData = getTodayHydrationData()
        if (todayData.glassesDrank > 0) {
            val updatedData = todayData.copy(
                glassesDrank = todayData.glassesDrank - 1,
                lastUpdated = Date()
            )
            saveHydrationData(updatedData)
        }
    }
    

    fun saveHydrationSettings(settings: HydrationReminderSettings) {
        val settingsJson = gson.toJson(settings)
        prefs.edit().putString(KEY_HYDRATION_SETTINGS, settingsJson).apply()
    }
    
    fun getHydrationSettings(): HydrationReminderSettings {
        val settingsJson = prefs.getString(KEY_HYDRATION_SETTINGS, null)
        return if (settingsJson != null) {
            gson.fromJson(settingsJson, HydrationReminderSettings::class.java)
        } else {
            HydrationReminderSettings()
        }
    }
    

    fun getDailyGoal(): Int {
        return prefs.getInt(KEY_DAILY_GOAL, 8)
    }
    
    fun setDailyGoal(goal: Int) {
        prefs.edit().putInt(KEY_DAILY_GOAL, goal).apply()
    }
    
    fun isReminderEnabled(): Boolean {
        return prefs.getBoolean(KEY_REMINDER_ENABLED, false)
    }
    
    fun setReminderEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_REMINDER_ENABLED, enabled).apply()
    }
    
    fun getReminderInterval(): Int {
        return prefs.getInt(KEY_REMINDER_INTERVAL, 60)
    }
    
    fun setReminderInterval(intervalMinutes: Int) {
        prefs.edit().putInt(KEY_REMINDER_INTERVAL, intervalMinutes).apply()
    }
    

    fun clearAllData() {
        prefs.edit().clear().apply()
    }
    
    fun saveBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }
    
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }
    
    fun saveString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
    
    fun getString(key: String, defaultValue: String = ""): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }
    
    fun saveLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }
    
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return prefs.getLong(key, defaultValue)
    }
    
    fun exportData(): String {
        val exportData = mapOf(
            "habits" to getHabits(),
            "habitCompletions" to getHabitCompletions(),
            "moodEntries" to getMoodEntries(),
            "hydrationData" to getHydrationData(),
            "hydrationSettings" to getHydrationSettings(),
            "exportDate" to Date()
        )
        return gson.toJson(exportData)
    }
    

    fun saveUserProfile(profile: UserProfile) {
        val profileJson = gson.toJson(profile)
        prefs.edit().putString("user_profile", profileJson).apply()
    }
    
    fun getUserProfile(): UserProfile {
        val profileJson = prefs.getString("user_profile", null)
        return if (profileJson != null) {
            try {
                gson.fromJson(profileJson, UserProfile::class.java)
            } catch (e: Exception) {
                UserProfile()
            }
        } else {
            UserProfile()
        }
    }
    

    fun saveOnboardingComplete(isComplete: Boolean) {
        prefs.edit().putBoolean("onboarding_complete", isComplete).apply()
    }
    
    fun isOnboardingComplete(): Boolean {
        return prefs.getBoolean("onboarding_complete", false)
    }
    

    fun clearAllAppData() {
        prefs.edit().clear().apply()
    }
    
    // New User Management Methods
    fun isNewUser(): Boolean {
        return !getBoolean("user_data_exists", false)
    }
    
    fun markUserDataExists() {
        saveBoolean("user_data_exists", true)
        saveLong("first_app_launch", System.currentTimeMillis())
    }
    
    fun getFirstAppLaunchDate(): Long {
        return prefs.getLong("first_app_launch", System.currentTimeMillis())
    }
    
    fun hasCompletedWelcomeScreen(): Boolean {
        return getBoolean("welcome_screen_complete", false)
    }
    
    fun completeWelcomeScreen() {
        saveBoolean("welcome_screen_complete", true)
    }
    
    fun getDaysSinceFirstLaunch(): Int {
        val firstLaunch = getFirstAppLaunchDate()
        val currentTime = System.currentTimeMillis()
        val daysDiff = (currentTime - firstLaunch) / (24 * 60 * 60 * 1000)
        return daysDiff.toInt()
    }
    
    fun shouldShowNewUserHints(): Boolean {
        return getDaysSinceFirstLaunch() <= 7 && !getBoolean("new_user_hints_complete", false)
    }
    
    fun completeNewUserHints() {
        saveBoolean("new_user_hints_complete", true)
    }
    
    fun resetNewUserExperience() {
        prefs.edit()
            .remove("user_data_exists")
            .remove("welcome_screen_complete")
            .remove("new_user_hints_complete")
            .remove("first_app_launch")
            .apply()
    }
    

    fun resetUserFlow() {
        prefs.edit()
            .remove("is_logged_in")
            .remove("onboarding_complete")
            .remove("is_guest")
            .remove("user_email")
            .apply()
    }
    
    fun resetAllForNewUser() {
        resetUserFlow()
        resetNewUserExperience()
        clearAllData()
    }
    

    fun clearMoodEntries() {
        prefs.edit().remove(KEY_MOOD_ENTRIES).apply()
    }
    
    fun clearHabits() {
        prefs.edit()
            .remove(KEY_HABITS)
            .remove(KEY_HABIT_COMPLETIONS)
            .apply()
    }
    
    fun clearHydrationData() {
        prefs.edit()
            .remove(KEY_HYDRATION_DATA)
            .remove(KEY_HYDRATION_SETTINGS)
            .apply()
    }
    
    /**
     * Clear demo data that was previously generated
     */
    fun clearDemoData() {
        prefs.edit()
            .remove(KEY_HABITS)
            .remove(KEY_HABIT_COMPLETIONS)
            .remove("demo_data_generated")
            .apply()
    }
}
