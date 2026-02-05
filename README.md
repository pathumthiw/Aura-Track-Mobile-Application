# AuraTracks - Personal Wellness Companion

AuraTracks is a comprehensive Android wellness app designed to help users manage their daily health routines and track their wellness journey. Built with Kotlin and Android Studio, the app combines multiple features to promote personal wellness.

## Features

### 1. Daily Habit Tracker
- Add, edit, and delete daily wellness habits
- Track completion progress for each day
- Visual progress indicators
- Habit completion statistics

### 2. Mood Journal with Emoji Selector
- Log mood entries with date/time
- 8 different emoji options (Happy, Sad, Angry, Excited, Calm, Tired, Stressed, Neutral)
- Add optional notes to mood entries
- View mood history in chronological order
- Share mood entries with others

### 3. Hydration Reminder
- Track daily water intake
- Set customizable daily goals
- Visual progress tracking with circular progress indicator
- Smart notifications using WorkManager
- Configurable reminder intervals
- Automatic reminder scheduling

### 4. Advanced Features
- **Sensor Integration**: Shake detection for quick mood entries
- **Data Export**: Export all app data for backup
- **Responsive UI**: Adapts to different screen sizes and orientations
- **Data Persistence**: All data stored using SharedPreferences

## Technical Implementation

### Architecture
- **Fragments/Activities**: Clean separation of concerns
- **Navigation Component**: Bottom navigation for phones, side navigation for tablets
- **MVVM Pattern**: ViewModels for data management
- **RecyclerView**: Efficient list rendering

### Data Persistence
- **SharedPreferences**: For storing user data without databases
- **Gson**: JSON serialization for complex data structures
- **Data Models**: Well-structured data classes for habits, moods, and hydration

### Notifications & Services
- **WorkManager**: Background task scheduling for reminders
- **Notification Channels**: Proper notification management
- **Boot Receiver**: Restart reminders after device reboot
- **Foreground Service**: Reliable reminder delivery

### UI/UX Design
- **Material Design 3**: Modern, accessible interface
- **Responsive Layouts**: Different layouts for phones and tablets
- **Landscape Support**: Optimized layouts for landscape orientation
- **Color-coded Sections**: Intuitive visual organization

## Project Structure

```
app/src/main/java/com/nutriwise/auratracks/
├── MainActivity.kt                 # Main activity with navigation
├── fragments/                      # UI fragments
│   ├── HabitsFragment.kt          # Habit tracking
│   ├── MoodFragment.kt            # Mood journaling
│   ├── HydrationFragment.kt       # Water tracking
│   └── SettingsFragment.kt        # App settings
├── adapters/                       # RecyclerView adapters
│   ├── HabitsAdapter.kt           # Habit list adapter
│   └── MoodAdapter.kt             # Mood list adapter
├── models/                         # Data models
│   ├── Habit.kt                   # Habit data class
│   ├── MoodEntry.kt               # Mood data class
│   └── HydrationData.kt           # Hydration data class
├── data/                          # Data management
│   └── SharedPreferencesHelper.kt # Data persistence
├── services/                      # Background services
│   └── HydrationReminderService.kt # Reminder service
├── receivers/                     # Broadcast receivers
│   └── BootReceiver.kt            # Boot completion receiver
└── sensors/                       # Sensor integration
    └── ShakeDetector.kt           # Shake detection
```

## Dependencies

- **AndroidX Core**: Core Android libraries
- **Material Design**: UI components
- **Navigation Component**: Fragment navigation
- **WorkManager**: Background task scheduling
- **Gson**: JSON serialization
- **MPAndroidChart**: Chart visualization (for future enhancements)

## Installation & Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on Android device/emulator

## Permissions

- `POST_NOTIFICATIONS`: For hydration reminders
- `WAKE_LOCK`: For background service operation
- `RECEIVE_BOOT_COMPLETED`: For restarting reminders after reboot
- `VIBRATE`: For notification feedback

## Usage

### Adding Habits
1. Navigate to the Habits tab
2. Tap the + button
3. Enter habit name and optional description
4. Save the habit

### Logging Mood
1. Go to the Mood Journal tab
2. Select an emoji that represents your current mood
3. Add an optional note
4. Tap Save

### Tracking Hydration
1. Open the Hydration tab
2. Use + and - buttons to track water intake
3. Set your daily goal using the slider
4. Enable reminders for regular hydration

### Quick Mood Entry (Shake Detection)
1. Enable shake detection in Settings
2. Shake your device when you want to log a mood
3. Select your mood from the popup dialog

## Future Enhancements

- Home screen widget showing daily progress
- Mood trend charts using MPAndroidChart
- Step counter integration
- Social sharing features
- Cloud backup and sync
- Habit streaks and achievements

## Requirements Met

✅ **Daily Habit Tracker** - Complete with add/edit/delete functionality  
✅ **Mood Journal with Emoji Selector** - 8 emoji options with date/time logging  
✅ **Hydration Reminder** - WorkManager-based notifications with customizable intervals  
✅ **Advanced Feature** - Sensor integration (shake detection)  
✅ **Data Persistence** - SharedPreferences for all user data  
✅ **Responsive UI** - Adapts to phones, tablets, portrait, and landscape  
✅ **Navigation** - Bottom navigation for phones, side navigation for tablets  
✅ **Code Quality** - Well-organized, documented, and follows Android best practices  

## Screenshots

The app features a clean, modern interface with:
- Intuitive navigation between sections
- Visual progress indicators
- Material Design 3 components
- Responsive layouts for different screen sizes
- Color-coded sections for easy identification

## License

This project is developed as an academic assignment and is for educational purposes only.
