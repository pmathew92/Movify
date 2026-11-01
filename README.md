# Movify

Movify is an Android app for discovering movies, with posters, release dates, and
movie overviews powered by The Movie Database (TMDB).

Built with Kotlin, Jetpack Compose, and MVVM, the app supports light and dark
themes and handles loading, empty results, and network errors with retry.

## Work in progress

The app is being migrated from XML-based views to Jetpack Compose. The movie feed
now uses Compose; search and the remaining UI migration are still in progress.

## Run locally

Open the project in Android Studio, copy `secrets.properties.example` to
`secrets.properties`, and add your TMDB API key:

```properties
TMDB_API_KEY=your_tmdb_api_key
```

Run the `app` configuration on a device or emulator with Android 6.0 (API 23) or
newer. Keep `secrets.properties` out of version control.

## Credits

Movie data and images are provided by TMDB. This app is not endorsed or certified
by TMDB.
