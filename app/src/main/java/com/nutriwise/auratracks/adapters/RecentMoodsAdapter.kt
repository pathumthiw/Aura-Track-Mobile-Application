package com.nutriwise.auratracks.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nutriwise.auratracks.databinding.ItemRecentMoodBinding
import com.nutriwise.auratracks.models.MoodEntry
import java.text.SimpleDateFormat
import java.util.*


class RecentMoodsAdapter(
    private var moods: List<MoodEntry>
) : RecyclerView.Adapter<RecentMoodsAdapter.RecentMoodViewHolder>() {
    
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentMoodViewHolder {
        val binding = ItemRecentMoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecentMoodViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: RecentMoodViewHolder, position: Int) {
        holder.bind(moods[position])
    }
    
    override fun getItemCount(): Int = moods.size
    

    fun updateMoods(newMoods: List<MoodEntry>) {
        moods = newMoods
        notifyDataSetChanged()
    }
    

    inner class RecentMoodViewHolder(
        private val binding: ItemRecentMoodBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(mood: MoodEntry) {
            binding.moodEmoji.text = mood.emoji
            

            val noteText = if (mood.note.isNotEmpty()) {
                mood.note
            } else {
                "No note added"
            }
            binding.moodNote.text = noteText
            

            val now = Date()
            val diff = now.time - mood.time.time
            val hours = diff / (1000 * 60 * 60)
            val minutes = (diff % (1000 * 60 * 60)) / (1000 * 60)
            
            val timeText = when {
                hours > 0 -> "${hours}h ago"
                minutes > 0 -> "${minutes}m ago"
                else -> "Just now"
            }
            
            binding.moodTime.text = timeText
        }
    }
}
