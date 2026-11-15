# Lucidia - Native Android Lucid Dreaming Companion

Lucidia is a native Android application engineered to help dreamers achieve, sustain, and track lucidity during sleep.

Inspired by the circadian sleep principles of **Twilight** and the interconnected knowledge representation of **Obsidian**, Lucidia combines non-disruptive awakening mechanisms with deep visual dream pattern mapping.

---

## Key Features

### 1. Wake-Back-To-Bed (WBTB) Alarm with Auto-Dismissal
- **Zero-Movement Auto-Dismissal**: Configurable auto-dismissal (5s, 10s, 15s, 30s, 60s) stops the alarm automatically so the dreamer wakes up gently without moving a muscle or opening their eyes, preserving the hypnagogic state required for **WILD** (Wake-Induced Lucid Dream) and **MILD** induction.
- **Gentle Volume Crescendo**: Starts with a soft chime and slowly rises, preventing cortisol/heart rate spikes during REM sleep.

### 2. Twilight Sleep & Melatonin Shield
- **Circadian Warmth Filter**: Direct warm/red spectrum screen overlay inspired by Twilight that filters out 100% of disruptive blue wavelengths.
- Protects melatonin production when checking dream notes or times in the middle of the night.

### 3. Obsidian-Style Dream Knowledge Graph
- **Interactive Force-Directed Node Graph**: Visualizes all recorded dreams, characters, locations, and recurring dream signs (`#flying`, `#mirror`, `#clock`, `#water`).
- **Hub Detection**: Recurring dream signs naturally emerge as large attractor hubs, helping users discover their personal dream cues to trigger lucidity.
- Supports pinch-to-zoom, pan, node isolation, and dynamic physics.

### 4. Lucid Dream Journal & Streak Tracking
- Fast dream recording with lucidity levels (Normal, Semi-Lucid, Lucid) and dream sign tagging.
- 7-day weekly visual streak tracker directly on the home dashboard.

---

## Design System

Designed around the warm, friendly, retro-futuristic nighttime aesthetic from `Page 2.png`:
- **Night Sky**: Deep indigo and midnight navy (`#1F243A` to `#151828`) with moon craters and twinkling stars.
- **Surfaces**: Soft curved turquoise/teal containers (`#5AB3AF`).
- **Alarms & Streaks**: Coral/peach pills (`#FF7E55`) with tactile embossed toggles.
- **Dock**: Floating teal pill dock housing 4 quick circular coral navigation buttons:
  1. **Clock** - Alarms & Streak Tracker
  2. **Journal** - Dream Diary & Tags
  3. **Graph** - Obsidian-style Dream Network
  4. **Settings** - Twilight Filter & Auto-Dismissal Timer

---

## Automated CI/CD (GitHub Actions)

This repository includes automated builds via `.github/workflows/build.yml`.
Every push builds the Android Debug APK on `ubuntu-latest` with JDK 17 and uploads the APK artifact for instant installation.
