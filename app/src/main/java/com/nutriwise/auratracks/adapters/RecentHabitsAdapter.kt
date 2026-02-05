package com.nutriwise.auratracks.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nutriwise.auratracks.databinding.ItemRecentHabitBinding
import com.nutriwise.auratracks.models.Habit
import com.nutriwise.auratracks.models.HabitCompletion
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying recent habit completions
 */
class RecentHabitsAdapter(
    private var habits: List<Habit>,
    private var completions: List<HabitCompletion>
) : RecyclerView.Adapter<RecentHabitsAdapter.RecentHabitViewHolder>() {
    
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentHabitViewHolder {
        val binding = ItemRecentHabitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecentHabitViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: RecentHabitViewHolder, position: Int) {
        try {
            if (position < habits.size && position < completions.size) {
                holder.bind(habits[position], completions[position])
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun getItemCount(): Int = minOf(habits.size, completions.size)
    
    /**
     * Update the list of recent habits
     */
    fun updateHabits(newHabits: List<Habit>, newCompletions: List<HabitCompletion>) {
        habits = newHabits
        completions = newCompletions
        notifyDataSetChanged()
    }
    
    /**
     * ViewHolder for recent habit items
     */
    inner class RecentHabitViewHolder(
        private val binding: ItemRecentHabitBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(habit: Habit, completion: HabitCompletion) {
            try {
                binding.habitName.text = habit.name
                binding.completionIcon.text = "✅"
                
                // Format completion time
                val timeText = if (completion.completedAt != null) {
                    val now = Date()
                    val diff = now.time - completion.completedAt.time
                    val hours = diff / (1000 * 60 * 60)
                    val minutes = (diff % (1000 * 60 * 60)) / (1000 * 60)
                    
                    when {
                        hours > 0 -> "${hours}h ago"
                        minutes > 0 -> "${minutes}m ago"
                        else -> "Just now"
                    }
                } else {
                    "Today"
                }
                
                binding.habitTime.text = timeText
            } catch (e: Exception) {
                e.printStackTrace()
                // Set default values if binding fails
                try {
                    binding.habitName.text = "Habit"
                    binding.completionIcon.text = "✅"
                    binding.habitTime.text = "Today"
                } catch (fallbackException: Exception) {
                    fallbackException.printStackTrace()
                }
            }
        }
    }
}
