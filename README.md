# Nutrition Tracking App

A mobile Android application designed to help users track nutrition data while allowing clinicians to monitor and support multiple users through a dedicated dashboard.

## Features
- User nutrition logging and tracking
- Clinician dashboard for viewing multiple users' data
- Category-based food filtering
- AI-assisted insights for trends and summaries
- Multi-user support with persistent local storage

## Tech Stack
- Kotlin
- Android
- MVVM architecture
- Room Database
- LiveData

## Architecture
The app follows the MVVM pattern to separate UI, business logic, and data layers. Room is used for local persistence, with LiveData enabling reactive UI updates and lifecycle-aware data handling.

## What I Learned
- Designing Android apps with clear separation of concerns using MVVM
- Managing local persistence and data relationships with Room
- Handling multiple user roles within a single application
- Building maintainable, testable Android code
