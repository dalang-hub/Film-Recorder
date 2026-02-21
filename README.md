# FilmRecorder

A modern film photography assistant app for Android that helps photographers manage their film rolls and track shot details.

## Features

- **Film Roll Management**: Create and manage multiple film rolls with custom names and ISO settings
- **Shot Tracking**: Record camera settings (aperture, shutter speed) for each shot
- **Photo Notes**: Add notes and timestamps to your shots
- **In-App Camera**: Take photos directly within the app
- **Organized View**: Clean list view of all film rolls and shots

## UI Design

The app features a modern, clean UI design inspired by Google Maps:
- Clean color palette with Google Blue (#4285F4) as primary color
- Rounded buttons and cards for a friendly, approachable look
- Light gray background with white surfaces for better readability
- Material Design components throughout

## Screenshots

| Main Screen | Add Roll | Shot List |
|-------------|----------|-----------|
| Film roll list | Create new roll | View shots |

## Tech Stack

- **Language**: Java
- **Architecture**: MVVM
- **Database**: Room (SQLite)
- **UI Components**: Material Design Components
- **Camera**: Android Camera API

## Build Instructions

```bash
# Clone the repository
git clone https://github.com/dalang-hub/Film-Recorder.git
cd Film-Recorder

# Build the app
./gradlew build

# Install debug version
./gradlew installDebug
```

## Requirements

- Android Studio Arctic Fox or later
- minSdkVersion 21 (Android 5.0 Lollipop)
- targetSdkVersion 34 (Android 14)
- JDK 11 or higher

## Project Structure

```
app/
├── src/main/
│   ├── java/com/ZhouJason/filmrecorder/
│   │   ├── MainActivity.java          # Main entry point
│   │   ├── AddRollActivity.java       # Add new film roll
│   │   ├── ShotListActivity.java      # View shots in a roll
│   │   ├── FilmRoll.java              # Film roll entity
│   │   ├── Shot.java                  # Shot entity
│   │   ├── AppDatabase.java           # Room database
│   │   ├── FilmRollDao.java           # Film roll DAO
│   │   ├── ShotDao.java               # Shot DAO
│   │   ├── FilmRollAdapter.java       # RecyclerView adapter
│   │   └── ShotAdapter.java           # RecyclerView adapter
│   ├── res/
│   │   ├── layout/                    # XML layouts
│   │   ├── drawable/                  # Icons and shapes
│   │   └── values/                    # Colors, themes, strings
│   └── AndroidManifest.xml
└── build.gradle
```

## License

This project is open source and available under the MIT License.

## Author

- GitHub: [@dalang-hub](https://github.com/dalang-hub)