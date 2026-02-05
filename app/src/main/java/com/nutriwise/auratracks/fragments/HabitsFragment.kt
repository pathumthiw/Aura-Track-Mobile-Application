package com.nutriwise.auratracks.fragments

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nutriwise.auratracks.R
import com.nutriwise.auratracks.adapters.HabitsAdapter
import com.nutriwise.auratracks.data.SharedPreferencesHelper
import com.nutriwise.auratracks.databinding.FragmentHabitsBinding
import com.nutriwise.auratracks.databinding.DialogAddHabitModernBinding
import com.nutriwise.auratracks.models.Habit
import java.text.SimpleDateFormat
import java.util.*


class HabitsFragment : Fragment() {
    
    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")
    
    private lateinit var prefsHelper: SharedPreferencesHelper
    private lateinit var habitsAdapter: HabitsAdapter
    private val habits = mutableListOf<Habit>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            if (!isAdded || context == null) return
            
            prefsHelper = SharedPreferencesHelper(requireContext())
            setupRecyclerView()
            setupClickListeners()
            loadHabits()
            updateProgress()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to load habits", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun setupRecyclerView() {
        try {
            if (!isAdded || context == null || _binding == null) return
            
            habitsAdapter = HabitsAdapter(
                habits = habits,
                prefsHelper = prefsHelper,
                onHabitToggle = { habit -> toggleHabit(habit) },
                onHabitEdit = { habit -> editHabit(habit) },
                onHabitDelete = { habit -> deleteHabit(habit) }
            )
            
            binding.habitsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.habitsRecyclerView.adapter = habitsAdapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupClickListeners() {
        try {
            if (!isAdded || context == null || _binding == null) return
            
            binding.fabAddHabit.setOnClickListener {
                showAddHabitDialog()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun loadHabits() {
        try {
            if (!isAdded || context == null || _binding == null) return
            
            habits.clear()
            val allHabits = prefsHelper.getHabits().filter { it.isActive }
            habits.addAll(allHabits)
            
            // Debug: Log habit categories
            allHabits.forEach { habit ->
                android.util.Log.d("HabitsFragment", "Habit: ${habit.name}, Category: ${habit.category}")
            }
            
            // Recreate adapter with updated data to ensure categories are displayed correctly
            habitsAdapter = HabitsAdapter(
                habits = habits,
                prefsHelper = prefsHelper,
                onHabitToggle = { habit -> toggleHabit(habit) },
                onHabitEdit = { habit -> editHabit(habit) },
                onHabitDelete = { habit -> deleteHabit(habit) }
            )
            binding.habitsRecyclerView.adapter = habitsAdapter
            
            updateEmptyState()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun updateProgress() {
        try {
            if (!isAdded || context == null || _binding == null) return
            
            val completedHabits = prefsHelper.getHabitCompletions()
                .count { it.isCompleted && it.date == getCurrentDateString() }
            val totalHabits = habits.size
            
            binding.progressText.text = "$completedHabits/$totalHabits completed"
            binding.progressBar.progress = if (totalHabits > 0) {
                (completedHabits * 100 / totalHabits)
            } else {
                0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun updateEmptyState() {
        try {
            if (!isAdded || context == null || _binding == null) return
            
            binding.emptyState.visibility = if (habits.isEmpty()) View.VISIBLE else View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun toggleHabit(habit: Habit) {
        try {
            if (!isAdded || context == null) return
            
            val isCompleted = prefsHelper.toggleHabitCompletion(habit.id, Date())
            updateProgress()
            
            // Notify that habits have changed for analytics update
            prefsHelper.saveBoolean("habits_data_changed", true)
            
            val message = if (isCompleted) {
                "${habit.name} completed!"
            } else {
                "${habit.name} not completed"
            }
            
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun editHabit(habit: Habit) {
        try {
            Toast.makeText(requireContext(), "Editing \"${habit.name}\"", Toast.LENGTH_SHORT).show()
            showAddHabitDialog(habit)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun deleteHabit(habit: Habit) {
        try {
            if (!isAdded || context == null) return
            
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Habit")
                .setMessage("Are you sure you want to delete \"${habit.name}\"?\n\nThis action cannot be undone and will remove all completion history for this habit.")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Delete") { _, _ ->
                    try {
                        prefsHelper.deleteHabit(habit.id)
                        Toast.makeText(requireContext(), "✅ Habit \"${habit.name}\" deleted successfully", Toast.LENGTH_SHORT).show()
                        loadHabits()
                        updateProgress()
                        
                        // Notify that habits have changed for analytics update
                        prefsHelper.saveBoolean("habits_data_changed", true)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Failed to delete habit", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun showAddHabitDialog(habit: Habit? = null) {
        try {
            if (!isAdded || context == null) return

            val dialogBinding = DialogAddHabitModernBinding.inflate(layoutInflater)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogBinding.root)
                .create()


            setupModernDialog(dialogBinding, dialog, habit)
            dialog.show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to open dialog", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupModernDialog(
        binding: DialogAddHabitModernBinding,
        dialog: AlertDialog,
        habit: Habit?
    ) {
        try {

            if (habit != null) {
                binding.root.findViewById<TextView>(R.id.habit_title)?.text = "Edit Habit"
            }


            binding.btnClose.setOnClickListener {
                dialog.dismiss()
            }


            // Initialize selectedCategory based on whether we're editing or creating
            var selectedCategory = if (habit != null) habit.category else "Health"
            
            if (habit != null) {
                binding.etHabitName.setText(habit.name)
                binding.etHabitDescription.setText(habit.description)
                
                // Set the correct category based on habit data
                when (habit.category) {
                    "Health" -> binding.chipHealth.isChecked = true
                    "Fitness" -> binding.chipFitness.isChecked = true
                    "Productivity" -> binding.chipProductivity.isChecked = true
                    "Mindfulness" -> binding.chipMindfulness.isChecked = true
                    else -> binding.chipHealth.isChecked = true
                }
            } else {
                // Set default category for new habits
                binding.chipHealth.isChecked = true
            }
            binding.chipGroupCategory.setOnCheckedStateChangeListener { group, checkedIds ->
                selectedCategory = when {
                    checkedIds.contains(R.id.chip_health) -> "Health"
                    checkedIds.contains(R.id.chip_fitness) -> "Fitness"
                    checkedIds.contains(R.id.chip_productivity) -> "Productivity"
                    checkedIds.contains(R.id.chip_mindfulness) -> "Mindfulness"
                    else -> "Health"
                }
                android.util.Log.d("HabitsFragment", "Category changed to: $selectedCategory")
            }


            binding.tvSelectedTime.setOnClickListener {
                val calendar = Calendar.getInstance()
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        val timeString = String.format("%02d:%02d", hourOfDay, minute)
                        binding.tvSelectedTime.text = timeString
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            }


            binding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }


            binding.btnSave.setOnClickListener {
                try {
                    val name = binding.etHabitName.text.toString().trim()
                    val description = binding.etHabitDescription.text.toString().trim()

                    // Debug: Log selected category
                    android.util.Log.d("HabitsFragment", "Saving habit with category: $selectedCategory")
                    
                    // Get selected category directly from chip group as fallback
                    val actualSelectedCategory = when {
                        binding.chipHealth.isChecked -> "Health"
                        binding.chipFitness.isChecked -> "Fitness"
                        binding.chipProductivity.isChecked -> "Productivity"
                        binding.chipMindfulness.isChecked -> "Mindfulness"
                        else -> "Health"
                    }
                    android.util.Log.d("HabitsFragment", "Actual selected category from chips: $actualSelectedCategory")

                    if (name.isEmpty()) {
                        Toast.makeText(requireContext(), "Please enter a habit name", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                            if (habit != null) {

                                val updatedHabit = habit.copy(
                                    name = name,
                                    description = description,
                                    category = actualSelectedCategory
                                )
                                prefsHelper.updateHabit(updatedHabit)
                                Toast.makeText(requireContext(), "✅ Habit \"$name\" updated successfully!", Toast.LENGTH_SHORT).show()
                                
                                // Notify that habits have changed for analytics update
                                prefsHelper.saveBoolean("habits_data_changed", true)
                            } else {

                                val newHabit = Habit(
                                    id = UUID.randomUUID().toString(),
                                    name = name,
                                    description = description,
                                    category = actualSelectedCategory,
                                    createdAt = Date(),
                                    isActive = true
                                )
                                prefsHelper.addHabit(newHabit)
                                Toast.makeText(requireContext(), "✅ Habit \"$name\" added successfully!", Toast.LENGTH_SHORT).show()
                            }

                    dialog.dismiss()
                    loadHabits()
                    updateProgress()
                    
                    // Notify that habits have changed for analytics update
                    prefsHelper.saveBoolean("habits_data_changed", true)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Failed to save habit", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun getCurrentDateString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}