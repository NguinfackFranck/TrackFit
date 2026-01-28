## FitTrack – Android Fitness Tracking Application
## Overview

FitTrack is an Android-based fitness tracking application designed to help users monitor and manage their daily physical activities.
The app enables users to track steps, calories burned, and active time, while setting personalized goals and viewing progress through a structured dashboard.

FitTrack combines local data persistence (SQLite) with cloud synchronization (Firebase) to ensure data availability across devices after authentication.

## Project Objectives

Design and implement a mobile fitness tracking solution

Apply software engineering principles (separation of concerns, persistence, validation)

Combine local storage and cloud services for data reliability

Provide a clear, user-friendly interface for daily fitness monitoring

## Key Features

User authentication (Firebase Authentication)

Personal profile management (age, height, weight, gender)

Daily activity tracking

Steps

Calories burned

Active time

Custom daily goal setting

Progress visualization dashboard

Activity history

Cloud synchronization and restoration for new devices

Offline-first design using SQLite

System Architecture

Presentation Layer

Activities & XML layouts (Dashboard, Login, Register, Daily Input)

Business Logic Layer

Validators

FirebaseSyncService

Data Layer

SQLite (FitnessDatabaseHelper)

Firebase Firestore (user data & daily stats)

## Technologies Used

Language: Java

Platform: Android SDK

Local Storage: SQLite, SharedPreferences

Cloud Services: Firebase Authentication, Firebase Firestore

Architecture: Activity-based with service abstraction

Testing (planned): JUnit, Robolectric

## Installation & Setup

Clone the repository:

git clone (https://github.com/NguinfackFranck/TrackFit)


Open the project in Android Studio

Configure Firebase (Authentication + Firestore)

Run the app on an Android emulator or physical device

## Usage

Register or log in using email and password

Complete personal profile (first-time users only)

Set daily fitness goals

Log daily activity data

View progress and history on the dashboard

Data is automatically synced to Firebase

## Future Improvements

Sensor-based automatic step tracking

Charts and analytics

Push notifications & reminders

Dark mode customization

Wearable device integration

## Author

Nguinfack Franck-Styve
Android Application Development Project
