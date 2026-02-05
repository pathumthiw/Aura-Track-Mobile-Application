package com.nutriwise.auratracks.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nutriwise.auratracks.R
import com.nutriwise.auratracks.adapters.MoodAdapter
import com.nutriwise.auratracks.data.SharedPreferencesHelper
import com.nutriwise.auratracks.databinding.FragmentMoodBinding
import com.nutriwise.auratracks.models.MoodEntry
import com.nutriwise.auratracks.models.MoodEmoji
import java.text.SimpleDateFormat
import java.util.*


class MoodFragment : Fragment() {
    
    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefsHelper: SharedPreferencesHelper
    private lateinit var moodAdapter: MoodAdapter
    private val moodEntries = mutableListOf<MoodEntry>()
    private var selectedEmoji: String? = null
    
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            prefsHelper = SharedPreferencesHelper(requireContext())
            setupRecyclerView()
            setupClickListeners()
            loadMoodEntries()
        } catch (e: Exception) {
            e.printStackTrace()

            showErrorMessage("Failed to load mood journal. Please restart the app.")
        }
    }
    
    override fun onResume() {
        super.onResume()
        try {

            if (_binding != null) {
                loadMoodEntries()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    

    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(
            moodEntries = moodEntries,
            onMoodShare = { moodEntry ->
                shareMood(moodEntry)
            }
        )
        
        binding.moodRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moodAdapter
        }
    }
    

    private fun setupClickListeners() {

        binding.buttonHappy.setOnClickListener { selectEmoji("😊") }
        binding.buttonSad.setOnClickListener { selectEmoji("😢") }
        binding.buttonAngry.setOnClickListener { selectEmoji("😠") }
        binding.buttonExcited.setOnClickListener { selectEmoji("🤩") }
        binding.buttonCalm.setOnClickListener { selectEmoji("😌") }
        binding.buttonTired.setOnClickListener { selectEmoji("😴") }
        binding.buttonStressed.setOnClickListener { selectEmoji("😰") }
        binding.buttonNeutral.setOnClickListener { selectEmoji("😐") }
        

        binding.buttonSaveMood.setOnClickListener {
            saveMoodEntry()
        }
        

        binding.clearMoodHistoryButton?.setOnClickListener {
            showClearHistoryDialog()
        }
    }
    

    private fun loadMoodEntries() {
        moodEntries.clear()
        moodEntries.addAll(prefsHelper.getMoodEntries().sortedByDescending { it.date })
        moodAdapter.notifyDataSetChanged()
        updateEmptyState()
    }
    

    private fun updateEmptyState() {
        binding.emptyState.visibility = if (moodEntries.isEmpty()) View.VISIBLE else View.GONE
    }
    

    private fun selectEmoji(emoji: String) {
        selectedEmoji = emoji
        

        val emojiButtons = listOf(
            binding.buttonHappy,
            binding.buttonSad,
            binding.buttonAngry,
            binding.buttonExcited,
            binding.buttonCalm,
            binding.buttonTired,
            binding.buttonStressed,
            binding.buttonNeutral
        )
        
        emojiButtons.forEach { button ->
            button.isSelected = false
        }
        

        val selectedButton = when (emoji) {
            "😊" -> binding.buttonHappy
            "😢" -> binding.buttonSad
            "😠" -> binding.buttonAngry
            "🤩" -> binding.buttonExcited
            "😌" -> binding.buttonCalm
            "😴" -> binding.buttonTired
            "😰" -> binding.buttonStressed
            "😐" -> binding.buttonNeutral
            else -> null
        }
        
        selectedButton?.isSelected = true
    }
    

    private fun saveMoodEntry() {
        if (selectedEmoji == null) {
            Toast.makeText(requireContext(), "Please select a mood", Toast.LENGTH_SHORT).show()
            return
        }
        
        val note = binding.editMoodNote.text.toString().trim()
        val now = Date()
        
        val moodEntry = MoodEntry(
            id = UUID.randomUUID().toString(),
            emoji = selectedEmoji!!,
            note = note,
            date = now,
            time = now
        )
        
        prefsHelper.addMoodEntry(moodEntry)
        

        selectedEmoji = null
        binding.editMoodNote.text?.clear()
        
        // Reset button selections
        val emojiButtons = listOf(
            binding.buttonHappy,
            binding.buttonSad,
            binding.buttonAngry,
            binding.buttonExcited,
            binding.buttonCalm,
            binding.buttonTired,
            binding.buttonStressed,
            binding.buttonNeutral
        )
        
        emojiButtons.forEach { button ->
            button.isSelected = false
        }
        
        Toast.makeText(requireContext(), "Mood saved successfully!", Toast.LENGTH_SHORT).show()
        loadMoodEntries()
        // Notify that mood data has been updated
        notifyMoodDataChanged()
    }
    

    private fun shareMood(moodEntry: MoodEntry) {
        val moodEmoji = MoodEmoji.fromEmoji(moodEntry.emoji)
        val moodName = moodEmoji?.displayName ?: "Unknown"
        
        val shareText = buildString {
            append("I'm feeling $moodName ${moodEntry.emoji}")
            if (moodEntry.note.isNotEmpty()) {
                append("\n\nNote: ${moodEntry.note}")
            }
            append("\n\nShared from AuraTracks - Your Wellness Companion")
        }
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        
        startActivity(Intent.createChooser(shareIntent, "Share your mood"))
    }
    

    private fun notifyMoodDataChanged() {

        try {
            prefsHelper.saveBoolean("mood_data_changed", true)
            prefsHelper.saveLong("last_mood_update", System.currentTimeMillis())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    private fun showClearHistoryDialog() {
        if (moodEntries.isEmpty()) {
            Toast.makeText(requireContext(), "No mood history to clear! 😊", Toast.LENGTH_SHORT).show()
            return
        }
        
        val count = moodEntries.size
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("🗑️ Clear Mood History")
            .setMessage("Are you sure you want to delete all $count mood entries? This action cannot be undone.")
            .setPositiveButton("Yes, Clear All") { _, _ ->
                clearMoodHistory()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setIcon(R.drawable.ic_delete)
            .show()
    }
    

    private fun clearMoodHistory() {
        try {
            prefsHelper.clearMoodEntries()
            moodEntries.clear()
            moodAdapter.notifyDataSetChanged()
            updateEmptyState()
            
            Toast.makeText(requireContext(), "✅ Mood history cleared successfully!", Toast.LENGTH_LONG).show()
            
            // Notify that mood data has been updated
            notifyMoodDataChanged()
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to clear mood history", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showErrorMessage(message: String) {
        try {
            if (_binding != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            android.util.Log.e("MoodFragment", "Failed to show error message: $message", e)
        }
    }
}
