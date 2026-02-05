package com.nutriwise.auratracks.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nutriwise.auratracks.databinding.ItemMoodBinding
import com.nutriwise.auratracks.models.MoodEntry
import java.text.SimpleDateFormat
import java.util.*


class MoodAdapter(
    private val moodEntries: List<MoodEntry>,
    private val onMoodShare: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {
    
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemMoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MoodViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(moodEntries[position])
    }
    
    override fun getItemCount(): Int = moodEntries.size
    

    inner class MoodViewHolder(
        private val binding: ItemMoodBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(moodEntry: MoodEntry) {
            binding.moodEmoji.text = moodEntry.emoji
            

            if (moodEntry.note.isNotEmpty()) {
                binding.moodNote.text = moodEntry.note
            } else {
                binding.moodNote.text = "No note added"
            }
            

            val dateString = dateFormat.format(moodEntry.date)
            val timeString = timeFormat.format(moodEntry.time)
            binding.moodDate.text = "$dateString at $timeString"
            

            binding.buttonShare.setOnClickListener {
                onMoodShare(moodEntry)
            }
        }
    }
}
