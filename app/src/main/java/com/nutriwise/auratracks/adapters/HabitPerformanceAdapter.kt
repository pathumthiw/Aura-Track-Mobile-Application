package com.nutriwise.auratracks.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nutriwise.auratracks.R
import com.nutriwise.auratracks.models.HabitAnalytics
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying individual habit performance in analytics
 */
class HabitPerformanceAdapter(
    private var habits: List<HabitAnalytics>
) : RecyclerView.Adapter<HabitPerformanceAdapter.HabitPerformanceViewHolder>() {
    
    private val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    
    class HabitPerformanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val habitName: TextView = itemView.findViewById(R.id.tv_habit_name)
        val completionRate: TextView = itemView.findViewById(R.id.tv_completion_rate)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        val currentStreak: TextView = itemView.findViewById(R.id.tv_current_streak)
        val longestStreak: TextView = itemView.findViewById(R.id.tv_longest_streak)
        val weeklyAvg: TextView = itemView.findViewById(R.id.tv_weekly_avg)
        val category: TextView = itemView.findViewById(R.id.tv_category)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitPerformanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit_performance, parent, false)
        return HabitPerformanceViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: HabitPerformanceViewHolder, position: Int) {
        val habit = habits[position]
        
        // Set habit name
        holder.habitName.text = habit.habitName
        
        // Set completion rate
        val completionRateText = "${habit.completionRate.toInt()}%"
        holder.completionRate.text = completionRateText
        
        // Set progress bar
        holder.progressBar.progress = habit.completionRate.toInt()
        
        // Set streak information
        holder.currentStreak.text = "${habit.currentStreak} days"
        holder.longestStreak.text = "${habit.longestStreak} days"
        
        // Set weekly average
        holder.weeklyAvg.text = String.format("%.1f", habit.averagePerWeek)
        
        // Set category
        holder.category.text = habit.category.displayName
        holder.category.setBackgroundColor(Color.parseColor(habit.category.color))
        
        // Set completion rate text color based on performance
        val completionRateColor = when {
            habit.completionRate >= 80 -> Color.parseColor("#4CAF50") // Green
            habit.completionRate >= 60 -> Color.parseColor("#FF9800") // Orange
            else -> Color.parseColor("#F44336") // Red
        }
        holder.completionRate.setTextColor(completionRateColor)
        
        // Set progress bar color
        holder.progressBar.progressTintList = android.content.res.ColorStateList.valueOf(completionRateColor)
    }
    
    override fun getItemCount(): Int = habits.size
    
    fun updateHabits(newHabits: List<HabitAnalytics>) {
        habits = newHabits.sortedByDescending { it.completionRate }
        notifyDataSetChanged()
    }
}
