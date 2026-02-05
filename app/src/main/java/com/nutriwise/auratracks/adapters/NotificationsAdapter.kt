package com.nutriwise.auratracks.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nutriwise.auratracks.databinding.ItemNotificationBinding
import com.nutriwise.auratracks.models.Notification
import com.nutriwise.auratracks.models.NotificationType
import java.text.SimpleDateFormat
import java.util.*


class NotificationsAdapter(
    private var notifications: List<Notification>,
    private val onNotificationClick: (Notification) -> Unit,
    private val onNotificationAction: (Notification, String) -> Unit
) : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {
    
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }
    
    override fun getItemCount(): Int = notifications.size
    

    fun updateNotifications(newNotifications: List<Notification>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }
    

    inner class NotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(notification: Notification) {
            try {

                binding.notificationIcon.text = notification.type.icon
                

                binding.notificationTitle.text = notification.title
                binding.notificationMessage.text = notification.message
                

                val timeText = formatTime(notification.timestamp)
                binding.notificationTime.text = timeText
                

                binding.unreadIndicator.visibility = if (notification.isRead) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                

                binding.root.alpha = if (notification.isRead) 0.7f else 1.0f
                

                setupActionButtons(notification)
                

                binding.root.setOnClickListener {
                    onNotificationClick(notification)
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        

        private fun setupActionButtons(notification: Notification) {
            try {
                when (notification.type) {
                    NotificationType.HABIT -> {
                        showActionButtons("View Habits", "view_habits")
                    }
                    NotificationType.MOOD -> {
                        showActionButtons("View Mood", "view_mood")
                    }
                    NotificationType.HYDRATION -> {
                        showActionButtons("View Hydration", "view_hydration")
                    }
                    NotificationType.ACHIEVEMENT -> {
                        showActionButtons("View", "view_habits")
                    }
                    NotificationType.REMINDER -> {
                        hideActionButtons()
                    }
                    else -> {
                        hideActionButtons()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        

        private fun showActionButtons(actionText: String, actionKey: String) {
            try {
                binding.actionButtonsLayout.visibility = View.VISIBLE
                binding.actionButton.text = actionText
                
                binding.actionButton.setOnClickListener {
                    onNotificationAction(notifications[adapterPosition], actionKey)
                }
                
                binding.dismissButton.setOnClickListener {
                    onNotificationAction(notifications[adapterPosition], "dismiss")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        

        private fun hideActionButtons() {
            binding.actionButtonsLayout.visibility = View.GONE
        }
        

        private fun formatTime(timestamp: Date): String {
            return try {
                val now = Date()
                val diff = now.time - timestamp.time
                val hours = diff / (1000 * 60 * 60)
                val minutes = (diff % (1000 * 60 * 60)) / (1000 * 60)
                val days = hours / 24
                
                when {
                    days > 0 -> "${days}d ago"
                    hours > 0 -> "${hours}h ago"
                    minutes > 0 -> "${minutes}m ago"
                    else -> "Just now"
                }
            } catch (e: Exception) {
                "Recently"
            }
        }
    }
}
