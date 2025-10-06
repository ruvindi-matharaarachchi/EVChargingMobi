# EV Charging Mobile App

A native Android application for EV charging station management, built with Kotlin and Android Studio.

## Features

### Authentication
- JWT-based authentication with secure token storage
- Support for EVOwner and StationOperator roles
- Automatic token refresh

### EV Owner Features
- **Dashboard**: View pending and approved booking counts
- **Nearby Stations**: Google Maps integration to find nearby charging stations
- **Reservations**: Create, modify, and cancel charging reservations
- **Business Rules**: Enforces 7-day booking window and 12-hour modification rule
- **QR Codes**: Display QR codes for approved bookings
- **Profile Management**: Update profile information and deactivate account

### Station Operator Features
- **QR Scanner**: Scan customer QR codes to verify bookings
- **Booking Completion**: Complete charging sessions
- **Session Management**: Handle charging session lifecycle

### Offline Support
- Local SQLite database for caching
- Offline queue for failed operations
- WorkManager for background sync when online

## Technical Stack

- **Language**: Kotlin
- **UI**: Material Design 3, ViewBinding
- **Database**: SQLite with custom DAOs (no Room)
- **Networking**: HttpURLConnection (no Retrofit)
- **Security**: EncryptedSharedPreferences for JWT storage
- **Background Tasks**: WorkManager
- **QR Code**: ZXing library
- **Maps**: Google Maps API

## Setup Instructions

### Prerequisites
- Android Studio Koala 2024.1.x or later
- JDK 17
- Android SDK 34
- Google Maps API key

### Backend Configuration
The app connects to a .NET 8 Web API backend. Update the base URL in `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:5034\"")
```

**URL Configuration:**
- **Emulator**: `http://10.0.2.2:5034` (default)
- **Physical Device**: `http://<PC_LAN_IP>:5034`
- **Production**: Update in release build variant

### Google Maps Setup
1. Get a Google Maps API key from [Google Cloud Console](https://console.cloud.google.com/)
2. Update `app/src/main/res/values/strings.xml`:
   ```xml
   <string name="google_maps_key">YOUR_GOOGLE_MAPS_API_KEY_HERE</string>
   ```

### Build and Run
1. Open the project in Android Studio
2. Sync Gradle files
3. Run the app on an emulator or physical device

## Project Structure

```
app/src/main/java/com/evcharge/mobile/
├── App.kt                          # Application class
├── security/
│   ├── TokenStore.kt               # JWT storage with encryption
│   └── AuthSession.kt              # Session management
├── data/
│   ├── local/
│   │   ├── DbHelper.kt             # SQLite database helper
│   │   ├── dao/                    # Data access objects
│   │   └── model/                  # Local data models
│   └── remote/
│       ├── HttpClient.kt           # HTTP client wrapper
│       ├── ApiRoutes.kt            # API endpoint definitions
│       └── *.kt                    # API service classes
├── domain/
│   ├── entities/                   # Domain models
│   └── repositories/               # Repository interfaces
├── sync/
│   ├── SyncWorker.kt               # Background sync worker
│   └── SyncWorkerFactory.kt        # Worker factory
├── util/
│   ├── Result.kt                   # Result wrapper
│   ├── Json.kt                     # JSON serialization
│   ├── DateTimeRules.kt            # Business rule validation
│   ├── LocationUtil.kt             # Location utilities
│   └── Time.kt                     # Time utilities
└── ui/
    ├── auth/                       # Authentication UI
    ├── owner/                      # EV Owner UI
    ├── operator/                   # Station Operator UI
    └── common/                     # Shared UI components
```

## API Integration

The app integrates with the following backend endpoints:

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Token refresh

### EV Owner
- `PUT /api/evowner/{nic}` - Update owner profile
- `POST /api/evowner/{nic}/deactivate` - Deactivate account

### Stations
- `GET /api/station` - Get all stations
- `GET /api/station/{id}` - Get station by ID
- `GET /api/station/nearby` - Get nearby stations

### Bookings
- `POST /api/booking` - Create booking
- `GET /api/booking/{id}` - Get booking details
- `GET /api/booking/owner/{nic}` - Get owner bookings
- `GET /api/booking/dashboard/{nic}` - Get dashboard counts
- `PUT /api/booking/{id}` - Update booking
- `DELETE /api/booking/{id}` - Cancel booking
- `POST /api/booking/complete` - Complete booking (operator)

## Business Rules

### 7-Day Rule
Reservations can only be made up to 7 days in advance.

### 12-Hour Rule
Modifications and cancellations must be made at least 12 hours before the start time.

## Offline Support

The app provides offline support through:

1. **Local Database**: SQLite for caching user data, stations, and bookings
2. **Sync Queue**: Failed operations are queued for retry
3. **WorkManager**: Background sync when network is available
4. **Smart Caching**: Local data is used when offline

## Security

- JWT tokens stored in EncryptedSharedPreferences
- Automatic token refresh on 401 responses
- Secure HTTP communication (HTTPS in production)
- Input validation and sanitization

## Testing

### Unit Tests
- `DateTimeRulesTest` - Business rule validation
- `JsonTest` - JSON serialization/deserialization
- `DaoTest` - Database operations

### Instrumented Tests
- `AuthFlowTest` - Authentication flow
- `BookingFlowTest` - Booking creation and management
- `SyncTest` - Offline sync functionality

## Troubleshooting

### Common Issues

1. **Network Connection**: Ensure backend is running on correct port
2. **Maps Not Loading**: Verify Google Maps API key is correct
3. **QR Scanner Not Working**: Check camera permissions
4. **Location Not Found**: Ensure location permissions are granted

### Debug Configuration

For debugging, the app uses cleartext HTTP for local development. This is configured in `network_security_config.xml`.

## Contributing

1. Follow Kotlin coding standards
2. Use Material Design 3 components
3. Implement proper error handling
4. Add unit tests for new features
5. Update documentation for API changes

## License

This project is part of the EV Charging Management System.
