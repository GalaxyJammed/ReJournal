<div align="center">

# ReJournal - Mood Logger

**An Android mental health journal app**

![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?logo=kotlin&logoColor=white)
![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Min SDK](https://img.shields.io/badge/Min%20SDK-26-blue)
![GitHub release](https://img.shields.io/github/v/release/GalaxyJammed/ReJournal?logo=github&label=GitHub%20release)
![License](https://img.shields.io/badge/License-MIT-lightgrey?logo=mit)
![Project Status](https://img.shields.io/badge/Project%20Status-WIP-blue)

</div>

---

## Introduction

A simple, private mood journaling app for Android - inspired by other mental health journal apps. Log how you're feeling each day, track patterns over time, and build a habit of daily check-ins, all with your data stored locally on your device.

<div align="center">
  <img width="100" height="100" alt="monarch-butterfly" src="https://github.com/user-attachments/assets/b54b264f-cc0d-4fde-acda-894b3faa9e54" />
  <br>
  <em>"Your soul will feel rejuvenated"</em>
</div>

This project was created as a way to let people freely use mental health journal logs. I've seen a lot of apps like these either contain ads or subscriptions and I thought that was unnecessary for something as important as mental health, so I decided to create my own free version of it. What started as a simple mood logger has since grown into a much fuller journaling companion - with goals, achievements, time capsules, and a fair bit of soul along the way.

---

## Features

### 🔔 Daily check-ins
- Log your **mood** (1–5 scale) along with **Energy**, **Productivity**, **Stress**, and **Sleep** sliders
- Tag your day with **activities** - pick from defaults, add your own custom tags, and give each one its own icon
- **Rich text notes**: bold, italic, underline, strikethrough, bullet/numbered lists, and custom text colors, plus a full-screen "Expand Note" mode
- Attach **photos** and **voice memos** to any day (that you can freely download to your own device)
- Mark a day as a **Favorite** with a tap of the heart icon
- Edit or delete any past entry, or back-fill a day you forgot to log

<div align="center">
<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/bcf5dccd-6f3f-47ef-8f79-6fe04f0ded99" width="400" alt="Image 1"/>
      <br>
      <sub>Day Logger 1 (The theme can be adjusted in settings!)</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/6fc1faf6-9065-4a76-a586-ded16b15fe2f" width="400" alt="Image 2">
      <br>
      <sub>Day Logger 2 (Log your day in every way possible!)</sub>
    </td>
  </tr>
</table>
</div>

### 😌 Visualize your mood
- **Month calendar view** - each day colored by mood, depending on the emoji/color you picked, with small icons marking Important and Favorite days
- **Year pixel view** - your entire year at a glance, one tiny colored square per day
- **Trend chart** - a mood line graph for the current month, properly spaced by real calendar dates, with filters for mood, sliders, and activities. Tap it to expand into a full, swipeable view of the entire month
- **Mood Constellation** - turn your trend into a starry, artistic night-sky view where star size reflects how much you logged that day, and save it as an image
- **Personality Tests** - Do one of our many personality tests to learn more about yourself

<div align="center">
<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/5321efd5-5c4a-45e6-bfe8-06c5860937ad" width="400" alt="Img1"/>
      <br>
      <sub>Main Calendar Screen</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/177b592a-ff98-47a6-b593-1e98e7efa9e0" width="400" alt="Image 2 description">
      <br>
      <sub>Stats Screen (Has more features than shown!)</sub>
    </td>
        </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/e8fdec63-b06a-4c30-87c8-b8cb0ccd81d9" width="400" alt="Image 2 description">
      <br>
      <sub>Trend Screen (Filters down below)</sub>
    </td>
  </tr>
</table>
</div>

### 📊 Stats & Insights
- Switch between **Week / Month / Year** views
- Mood breakdown chart, average mood/energy/productivity/stress/sleep
- Best & toughest day of the week/month/year (based on your averages)
- **Additional Stats**: most-logged activities, mood-by-activity ranking, activities on your best/worst days, and simple correlation insights (e.g. "Higher Sleep tends to line up with better mood days")
- Tap any mood on the breakdown chart to see **all-time patterns** for that specific mood - common activities, common weekdays, and average slider values

### 🎯 Goals, Achievements & Time Capsules
- **Goals**: pick a category (Fitness, Habits, Healthier Lifestyle, Growth, Break Bad Habits) and work toward a suggested goal, with up to 3 active at once
- **Achievements**: tiered, ever-growing milestones (entries logged, streaks, goals completed, and more) that unlock live as you use the app
- **Time Capsules**: write a message to your future self - deliver it the next time you log a specific mood, or on a specific future date

### 🔄 Habit building
- **Streak tracking** - current streak and personal best, shown right on the main screen
- **Daily reminder notifications** at a time you choose (exact alarm scheduling)
- **Important Days** - mark upcoming dates (birthdays, events, etc.) with a custom message and get notified the moment they arrive, or sync them automatically from your device google calendar
- **Rotating supportive messages** based on your recent mood trend (regardless of whether your mood is good or bad)
- **Random reflections** - an occasional gentle nudge comparing how you felt on this day last week/month
- **Widgets** - a tap-to-log mood widget, an average mood widget, and a goals widget, all matching your chosen theme

### 🔍 Search
- Filter your entire history by mood, activity tags, favorites, or exact slider values (Energy/Productivity/Stress/Sleep)
- Tap any result to jump straight to that day

### 🎨 Make it yours
- **Themes**: several built-in color themes plus full light/dark mode support
- **Mood Appearance**: switch between emoji or colored circles to represent mood, choose from preset color palettes or set your own custom hex colors, and even swap in your own custom emoji
- **Profile**: set a nickname (and optional age) for a more personal touch, including time-of-day greetings on the day logger

### 🔒 Privacy & Security
- Everything is stored **locally** - nothing leaves your device unless you choose to
- **App Lock**: protect the app with fingerprint, a 4-digit PIN, or both
- This app will never ask for any usage data. Everything from logging is only available to the user that downloaded the app. Nothing is shared.

### 💾 Your data, your control
- **Export to CSV** anytime, ready to open in Excel/Sheets or attach to an email/back it up to cloud storage of your choice
- **Full backup & restore**: export everything (entries, photos, and voice memos) as a single file, and import it back on this device or a new one
- **Calendar Sync**: automatically mark this month's device calendar events as Important Days

### ℹ️ About & Support
- **What's New**: a running changelog of updates, right inside the app
- **FAQ**: answers to common questions about how features work
- **Troubleshooting**: a built-in checklist for notification issues (permissions, battery optimization, autostart) to help track down why reminders might not be arriving
- **Update checker**: a friendly heads-up on the main screen when a newer version is available on GitHub

---

## Setup

1. Download the APK from [Releases](../../releases)
2. Click on the **+** or any day you'd like to log an entry

Simple and easy!

---

## Bug Reports/Suggestions
- Feel free to report any bug reports/suggestions by opening an [issue](https://github.com/GalaxyJammed/ReJournal/issues) or [discussion](https://github.com/GalaxyJammed/ReJournal/discussions) thread!

---
