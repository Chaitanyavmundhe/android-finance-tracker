# Finance Tracker (Android) - Webmitra.tech

A lightweight, distraction-free Android personal finance application built with Kotlin and Jetpack Compose.
Designed using **Google Stitch** (`projects/14249649889764616798`) and optimized for resource-constrained development machines (8GB RAM + Intel Celeron).

## Features
- **Landing Screen**: 1.8-second animated splash screen featuring the **Webmitra** logo with glowing pulse effect.
- **Design System**: **Vibrant Inter Theme** with deep pitch black canvas (`#090D14`), neon lime accents (`#A3E635`), vivid crimson (`#FF4D4D`), and crisp typography.
- **Two Categories Only**:
  - `Investment` (+ Green/Lime)
  - `Expense` (- Red/Crimson)
- **Timeframe Breakdown**:
  - `This Week`
  - `This Month`
  - `This Year`
- **Currency**: Indian Rupee (`₹`).
- **Live Sync & Dual-Tone Ratio Bar**: Real-time percentage comparison of Investment vs Spent.
- **Ledger Search**: Instant search through transaction notes and categories.
- **Privacy First**: 100% offline local SQLite database powered by Android Room.
- **Resource Optimized**:
  - Gradle JVM max heap capped to 1.5GB in `gradle.properties`.
  - Zero heavy background daemons or redundant IDE plugins.

## Architecture
- **Language**: Kotlin 1.9
- **UI Framework**: Jetpack Compose (Material 3)
- **Local Database**: Room Database 2.6
- **Architecture Pattern**: MVVM with Repository pattern and Kotlin StateFlow

## Running on Physical Android Device (Recommended for 8GB RAM + Celeron)
1. Enable **Developer Options** and **USB Debugging** on your Android smartphone.
2. Connect your phone via USB cable to your computer.
3. Verify connection:
   ```bash
   adb devices
   ```
4. Build and install:
   ```bash
   ./gradlew installDebug
   ```
