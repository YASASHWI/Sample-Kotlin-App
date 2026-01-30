# Passwordless Auth Android App

A passwordless authentication application using Email + OTP, built with Kotlin, Jetpack Compose, and Firebase Analytics.

## 1. OTP Generation and Expiry Handling
- **Generation**: OTPs are 6-digit random number strings generated locally using `kotlin.random.Random`.
- **Storage**: OTPs are stored in a `MutableMap<String, OtpEntry>` in the `OtpManager` singleton. The key is the email address.
- **Expiry**:
  - Each `OtpEntry` stores the `timestamp` of generation.
  - On validation, `System.currentTimeMillis()` is compared against the stored timestamp.
  - If the difference exceeds `60,000` ms (60 seconds), the OTP is considered expired.
- **Rules**:
  - Generating a new OTP overwrites the previous entry for that email (invalidating the old one).
  - Validating successfully clears the entry.

## 2. Data Structures
- **`MutableMap<String, OtpEntry>`**: Used for O(1) lookups of OTP data by email. `OtpEntry` holds the code, timestamp, and attempt count.
- **`StateFlow<AuthState>`**: Used in `AuthViewModel` to expose the current UI state in a reactive, lifecycle-aware manner.
- **`Sealed Class AuthState`**: Represents the mutually exclusive states of the UI (LoggedOut, OtpSent, LoggedIn), eliminating invalid intermediate states.

## 3. External SDK: Firebase Analytics
I chose **Firebase Analytics** as the external SDK.
- **Why**: It is the industry standard for Android analytics, offers free unlimited logging, and integrates seamlessly with Google Play Services.
- **Integration**:
  - Added the `com.google.gms.google-services` plugin and `firebase-analytics` dependency.
  - Created a wrapper `AnalyticsLogger` to abstract the `FirebaseAnalytics` instance.
  - **Note**: A dummy `google-services.json` is included to let the project build. For real data to appear in the Firebase Console, replace it with a valid file from your Firebase project.

## 4. AI Assistance Statement
- **AI Assisted**:
  - Boilerplate setup for Gradle and Manifest.
  - Generating the Compose UI hierarchies options.
  - Drafting the `OtpManager` logic to ensure all edge cases (expiry, attempts) were covered.
- **Manual Understanding**:
  - The architectural pattern (ViewModel + StateFlow).
  - The decision to use a `LaunchedEffect` for the session timer instead of a ViewModel ticker.
  - The mapping of requirements to specific implementation details.

## Edge Case Handling
- **Rotation**: Handled by storing state in `ViewModel`. `MainActivity` observes the `StateFlow`, so on rotation, it just re-renders the correct screen based on the current state (which survives).
- **Expired OTP**: Returns specific `OtpResult.Expired` state which updates the UI with an error message prompting resend.
- **Max Attempts**: Tracks attempts in `OtpEntry`. Returns `OtpResult.TooManyAttempts` triggered after 3 failed tries.

## Build Instructions
1. Open the project in Android Studio.
2. Sync Gradle.
3. Run on an Emulator or Device.
   - *Note: Since there is no backend, the OTP is printed to `System.out` (Logcat) for testing. Filter Logcat by "DEBUG" to see it.*
