# IRCTC Ticket Booking (CLI)

A simple Java CLI app for searching trains, booking seats, and managing user bookings backed by JSON files.

## Prerequisites
- Java 17+ (tested with Java 25)
- Bash shell (for the provided commands)
- Internet access for Gradle to download dependencies

## Project Structure
- `src/main/java/ticket/booking` – app source
  - `entities` – `User`, `Train`, `Ticket`
  - `service` – `UserBookingService`, `TrainService`
  - `util` – `UserServiceUtil` (BCrypt password helpers)
  - `localDb` – JSON data files (`users.json`, `trains.json`)
  - `Main.java` – CLI entry point

## Setup
1) Clone and enter the project directory
```bash
cd /home/raghu/Desktop/irctc
```

2) Build the project
```bash
./gradlew clean build
```

3) (Optional) Seed sample data
- Users: edit `src/main/java/ticket/booking/localDb/users.json`
- Trains: edit `src/main/java/ticket/booking/localDb/trains.json`

Example minimal trains file:
```json
[
  {
    "train_id": "bacs",
    "train_no": "12345",
    "seats": [[0,0,0],[0,0,0],[0,0,0]],
    "station_times": {"Bangalore": "08:00", "Jaipur": "12:00", "Delhi": "18:00"},
    "stations": ["Bangalore", "Jaipur", "Delhi"],
    "train_info": "Train ID: bacs Train No: 12345"
  }
]
```

## Run
You can run either via Gradle or via the installed distribution scripts.

- Run via Gradle (interactive):
```bash
./gradlew run
```

- Install distribution and run startup script:
```bash
./gradlew installDist
build/install/irctc/bin/irctc
```

Tip: If piping inputs is needed (non-interactive), you can feed a sequence. Example to immediately exit:
```bash
printf '7\n' | build/install/irctc/bin/irctc
```

## Features
- Sign up / Login (hashed passwords with BCrypt)
- Search trains by source and destination (case-insensitive order-aware)
- View bookings, Book a seat, Cancel a booking
- Data persisted to JSON files in `localDb`

## Troubleshooting
- Task 'run' not found:
  - Ensure the `application` plugin is enabled and `mainClass` is set in `build.gradle`.
- JSON file not found / empty data:
  - The app creates empty `users.json`/`trains.json` on first run if missing. Place them under `src/main/java/ticket/booking/localDb/`.
- Scanner input errors when running via `./gradlew run` with piped input:
  - Prefer the installed script: `./gradlew installDist && printf '7\n' | build/install/irctc/bin/irctc`.
- Permission denied on scripts:
  - Make executable: `chmod +x gradlew build/install/irctc/bin/irctc`.

## Common Commands
```bash
# Build
./gradlew clean build

# Run (interactive)
./gradlew run

# Install runnable distribution and run
./gradlew installDist
build/install/irctc/bin/irctc

# View problems report
xdg-open build/reports/problems/problems-report.html || true
```
