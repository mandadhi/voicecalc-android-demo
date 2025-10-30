VoiceCalc Android - Gemini powered (Pro scaffold)

Features:
- Voice input (Android SpeechRecognizer via Intent)
- Gemini REST integration (configure BuildConfig.GEMINI_API_KEY)
- Local simple expression evaluation (mXparser)
- Result + explanation display (LLM returns JSON or text)
- Room history skeleton
- Play Billing skeleton (lifetime unlock placeholder)
- Jetpack Compose UI with continuous & manual listening modes

IMPORTANT:
- For production, do NOT embed the Gemini key in the app. Use a secure server-side proxy.
- To test quickly, you can place your Gemini API key in app/build.gradle.kts buildConfigField or edit the file after generation.
- After generation, open the project in Android Studio and sync Gradle.

To push manually:
git remote add origin https://github.com/$FULL_REPO.git
git branch -M main
git push -u origin main
