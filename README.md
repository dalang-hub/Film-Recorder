# FilmRecorder | 胶片记录器

[English](#english) | [中文](#中文)

---

<a name="english"></a>
## English

A modern film photography assistant app for Android that helps photographers manage their film rolls and track shot details.

### Features

- **Film Roll Management**: Create and manage multiple film rolls with custom names and ISO settings
- **Shot Tracking**: Record camera settings (aperture, shutter speed) for each shot
- **Photo Notes**: Add notes and timestamps to your shots
- **In-App Camera**: Take photos directly within the app
- **Organized View**: Clean list view of all film rolls and shots

### UI Design

The app features a modern, clean UI design inspired by Google Maps:
- Clean color palette with Google Blue (#4285F4) as primary color
- Rounded buttons and cards for a friendly, approachable look
- Light gray background with white surfaces for better readability
- Material Design components throughout

### Tech Stack

- **Language**: Java
- **Architecture**: MVVM
- **Database**: Room (SQLite)
- **UI Components**: Material Design Components
- **Camera**: Android Camera API

### Build Instructions

```bash
# Clone the repository
git clone https://github.com/dalang-hub/Film-Recorder.git
cd Film-Recorder

# Build the app
./gradlew build

# Install debug version
./gradlew installDebug
```

### Requirements

- Android Studio Arctic Fox or later
- minSdkVersion 21 (Android 5.0 Lollipop)
- targetSdkVersion 34 (Android 14)
- JDK 11 or higher

### Project Structure

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

---

<a name="中文"></a>
## 中文

一款现代化的 Android 胶片摄影助手应用，帮助摄影师管理胶卷并记录拍摄详情。

### 功能特性

- **胶卷管理**：创建并管理多个胶卷，支持自定义名称和 ISO 设置
- **拍摄记录**：记录每张照片的相机设置（光圈、快门速度）
- **照片备注**：为照片添加备注和时间戳
- **内置相机**：直接在应用内拍摄照片
- **清晰列表**：整洁的胶卷和照片列表视图

### 界面设计

应用采用现代化的简洁 UI 设计，灵感来自 Google Maps：
- 以 Google 蓝 (#4285F4) 为主色调的清爽配色
- 圆润的按钮和卡片设计，友好易用
- 浅灰色背景搭配白色表面，提升可读性
- 全面采用 Material Design 组件

### 技术栈

- **开发语言**：Java
- **架构模式**：MVVM
- **数据库**：Room (SQLite)
- **UI 组件**：Material Design Components
- **相机**：Android Camera API

### 构建说明

```bash
# 克隆仓库
git clone https://github.com/dalang-hub/Film-Recorder.git
cd Film-Recorder

# 构建应用
./gradlew build

# 安装调试版本
./gradlew installDebug
```

### 环境要求

- Android Studio Arctic Fox 或更高版本
- minSdkVersion 21 (Android 5.0 Lollipop)
- targetSdkVersion 34 (Android 14)
- JDK 11 或更高版本

### 项目结构

```
app/
├── src/main/
│   ├── java/com/ZhouJason/filmrecorder/
│   │   ├── MainActivity.java          # 主入口
│   │   ├── AddRollActivity.java       # 添加新胶卷
│   │   ├── ShotListActivity.java      # 查看胶卷照片
│   │   ├── FilmRoll.java              # 胶卷实体类
│   │   ├── Shot.java                  # 照片实体类
│   │   ├── AppDatabase.java           # Room 数据库
│   │   ├── FilmRollDao.java           # 胶卷数据访问对象
│   │   ├── ShotDao.java               # 照片数据访问对象
│   │   ├── FilmRollAdapter.java       # 胶卷列表适配器
│   │   └── ShotAdapter.java           # 照片列表适配器
│   ├── res/
│   │   ├── layout/                    # XML 布局文件
│   │   ├── drawable/                  # 图标和形状
│   │   └── values/                    # 颜色、主题、字符串
│   └── AndroidManifest.xml
└── build.gradle
```

---

## License | 许可证

This project is open source and available under the MIT License.

本项目开源，采用 MIT 许可证。

## Author | 作者

- GitHub: [@dalang-hub](https://github.com/dalang-hub)