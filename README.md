<div align="center">

# ReJournal

**An Android mental health journal app**

![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?logo=kotlin&logoColor=white)
![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Min SDK](https://img.shields.io/badge/Min%20SDK-26-blue)
![GitHub release](https://img.shields.io/github/v/release/GalaxyJammed/ReJournal?logo=github&label=GitHub%20release)
![License](https://img.shields.io/badge/License-MIT-lightgrey?logo=mit)
![Project Status](https://img.shields.io/badge/Project%20Status-Unfinished-blue)

</div>

---

## Introduction

A simple, private mood journaling app for Android — inspired by other mental health journal apps. Log how you're feeling each day, track patterns over time, and build a habit of daily check-ins, all with your data stored locally on your device.

<div align="center">
  <img width="100" height="100" alt="monarch-butterfly" src="https://github.com/user-attachments/assets/971d7cd0-434d-4186-bbb3-20ecc1a10031" />
  <br>
  <em>"Your soul will feel rejuvenated"</em>
</div>

This project was created as a way to let people freely use mental health journal logs. I've seen alot of apps like these either contain ads or subscriptions and I thought that was unnecessary for something as important as mental health so I decided to create my own free version of it.

---

## Features

### 🔔 Daily check-ins
- Log your **mood** (1–5 scale) along with **Energy**, **Productivity**, **Stress**, and **Sleep** sliders
- Tag your day with activities - pick from defaults or add your own custom tags
- Optional notes for extra context
- Edit or delete any past entry, or back-fill a day you forgot to log

### 😌 Visualize your mood
- **Month calendar view** - each day colored by mood, depending on the emoji you picked
- **Year pixel view** - your entire year at a glance, one tiny colored square per day
- **Trend chart** - a 30-day mood line graph, properly spaced by real calendar dates (not just entry count)

### 🗂️ App Categories & Exclusions
- Assign any installed app to **Messages/Calls** or **Calendar**, or leave it untouched (doesn't trigger the popup)
- **Exclusions** - mark specific apps (games, sensitive apps, etc.) to be silently tracked with a small on-screen flower badge, with no popup or sound at all
- Both screens support live search across every installed app

### 📊 Stats & Insights
- Switch between **Week / Month / Year** views
- Mood breakdown chart, average mood/energy/productivity/stress/sleep
- Best & toughest day of the week/month/year (based on your averages)
- **Additional Stats**: most-logged activities, mood-by-activity ranking, activities on your best/worst days, and simple correlation insights (e.g. "Higher Sleep tends to line up with better mood days")

### 🔄 Habit building
- **Streak tracking** - current streak and personal best, shown right on the main screen
- **Daily reminder notifications** at a time you choose (exact alarm scheduling)
- Rotating supportive messages based on your recent mood trend (regardless if your mood is bad or not)

### 🔍 Search
- Filter your entire history by mood, activity tags (AND logic), or exact slider values (Energy/Productivity/Stress/Sleep)
- Tap any result to jump straight to that day

### 💾 Your data, your control
- Everything is stored **locally** in a Room (SQLite) database - nothing leaves your device unless you choose to
- **Export to CSV** anytime, ready to open in Excel/Sheets or attach to an email/back it up to cloud storage of your choice
- This app will never ask for any usage data. Everything from logging is only available to the user that downloaded the app. Nothing is shared.

---

## Setup

1. Download the APK from [Releases](../../releases)
2. Click on the **+** or any day you'd like to log an entry

Simple and easy!

---

## Bug Reports/Suggestions
- Feel free to report any bug reports/suggestions by opening an [issue](https://github.com/GalaxyJammed/ReJournal/issues) or [discussion](https://github.com/GalaxyJammed/ReJournal/discussions) thread!

---

## Project Notes
- This project focuses on databases, charts and the newest Jetpack Compose style of design instead of .xml
- Quotes/Naming inspiration by [Pokemon Rejuvenation](https://rejuvenation.wiki.gg/)
- Images on README.md by [FlatIcon](https://flaticon)
- Inspired by apps of the same nature that log mood entries
- This project is not affiliated with any of the websites/pieces of media mentioned above

---

## Currently working on
- Fingerprint/password access
- Cloud storage to export/import data (incase you want to move it)
- App Icon
