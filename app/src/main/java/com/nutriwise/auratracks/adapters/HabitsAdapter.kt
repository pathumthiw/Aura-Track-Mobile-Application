package com.nutriwise.auratracks.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.nutriwise.auratracks.R
import com.nutriwise.auratracks.databinding.ItemHabitModernBinding
import com.nutriwise.auratracks.models.Habit
import com.nutriwise.auratracks.data.SharedPreferencesHelper
import java.text.SimpleDateFormat
import java.util.*


class HabitsAdapter(
    private val habits: List<Habit>,
    private val prefsHelper: SharedPreferencesHelper,
    private val onHabitToggle: (Habit) -> Unit,
    private val onHabitEdit: (Habit) -> Unit,
    private val onHabitDelete: (Habit) -> Unit
) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {
    
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitModernBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HabitViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        try {
            if (position < habits.size) {
                holder.bind(habits[position])
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun getItemCount(): Int = habits.size
    
    inner class HabitViewHolder(
        private val binding: ItemHabitModernBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(habit: Habit) {
            try {
                binding.habitName.text = habit.name

                if (habit.description.isNotEmpty()) {
                    binding.habitDescription.text = habit.description
                    binding.habitDescription.visibility = View.VISIBLE
                } else {
                    binding.habitDescription.visibility = View.GONE
                }

                binding.habitDate.text = "Created: ${dateFormat.format(habit.createdAt)}"

                // Set category chip based on habit data
                android.util.Log.d("HabitsAdapter", "Setting category for ${habit.name}: ${habit.category}")
                binding.chipCategory.text = habit.category
                binding.chipCategory.setChipBackgroundColorResource(
                    when (habit.category) {
                        "Health" -> R.color.success
                        "Fitness" -> R.color.warning
                        "Productivity" -> R.color.info
                        "Mindfulness" -> R.color.primary_blue
                        else -> R.color.success
                    }
                )

                binding.tvStreak.text = "🔥 ${(1..30).random()} days"

                // Set completion state based on actual data
                binding.checkboxCompleted.setOnCheckedChangeListener(null)
                binding.checkboxCompleted.isChecked = isHabitCompletedToday(habit.id)

                binding.checkboxCompleted.setOnCheckedChangeListener { _, _ ->
                    try {
                        onHabitToggle(habit)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }



                // Debug: Check if buttons exist
                android.util.Log.d("HabitsAdapter", "Edit button: ${binding.buttonEdit != null}")
                android.util.Log.d("HabitsAdapter", "Delete button: ${binding.buttonDelete != null}")

                binding.buttonEdit.setOnClickListener {
                    try {
                        onHabitEdit(habit)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                binding.buttonDelete.setOnClickListener {
                    try {
                        onHabitDelete(habit)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()

                try {
                    binding.habitName.text = "Habit"
                    binding.habitDescription.text = ""
                    binding.habitDate.text = "Created: Today"
                } catch (fallbackException: Exception) {
                    fallbackException.printStackTrace()
                }
            }
        }
    }
    
    /**
     * Check if a habit is completed today
     */
    private fun isHabitCompletedToday(habitId: String): Boolean {
        return try {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val completions = prefsHelper.getHabitCompletions()
            completions.any { it.habitId == habitId && it.date == today && it.isCompleted }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}