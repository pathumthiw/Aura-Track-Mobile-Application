package com.nutriwise.auratracks.models

import java.util.Date

/**
 * Data class representing a mood entry
 * @param id Unique identifier for the mood entry
 * @param emoji Emoji representing the mood
 * @param note Optional note about the mood
 * @param date Date when the mood was recorded
 * @param time Time when the mood was recorded
 */
data class MoodEntry(
    val id: String,
    val emoji: String,
    val note: String = "",
    val date: Date = Date(),
    val time: Date = Date()
)

/**
 * Enum class for available mood emojis
 */
enum class MoodEmoji(val emoji: String, val displayName: String) {
    HAPPY("😊", "Happy"),
    SAD("😢", "Sad"),
    ANGRY("😠", "Angry"),
    EXCITED("🤩", "Excited"),
    CALM("😌", "Calm"),
    TIRED("😴", "Tired"),
    STRESSED("😰", "Stressed"),
    NEUTRAL("😐", "Neutral");

    companion object {
        fun fromEmoji(emoji: String): MoodEmoji? {
            return values().find { it.emoji == emoji }
        }
    }
}
