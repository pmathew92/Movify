# Movify

Movify is a Kotlin Android application built with MVVM, the repository pattern,
StateFlow, coroutines, AndroidX, Retrofit and Glide.
The application ID and namespace are `com.movify.app`.

## Architecture

```text
Activity + View Binding
    collects StateFlow<MovieListUiState> while STARTED
MainViewModel / SearchViewModel
    calls suspending MovieRepository methods
MovieRepositoryImpl
    uses the suspending NetworkInterface (Retrofit)
TMDB API
```

- Activities render state and forward user actions. They collect with
  `lifecycleScope` and `repeatOnLifecycle(STARTED)`, which stops collection while
  the screen is inactive and resumes with the latest state.
- ViewModels expose read-only `StateFlow` for loading, results, empty and error
  states. Kotlin data classes give UI states and movie models value equality.
  ViewModel state survives activity recreation.
- Requests and debounce timers run in `viewModelScope`. Cancellation is rethrown
  rather than rendered as an error. Clearing a ViewModel cancels its requests.
- Search debounces typing for 300 ms, cancels previous requests immediately when
  the query changes, handles clearing the query, and supports retry after errors.
- `MovieRepository` defines suspending discovery and search methods returning
  `List<Result>`. `MovieRepositoryImpl` supplies the API key, invokes Retrofit,
  and maps responses to movie lists, including missing or empty results. This
  remains the boundary for future local storage; current requests use the network.
- `AppContainer` creates one repository and ViewModel factory per application.
  Dependencies are passed through constructors. No DI framework is required.
  Activities obtain their ViewModels through `ViewModelProvider`.
- `MoviesAdapter` uses `ListAdapter` and `DiffUtil` to update the existing list.

All app and test sources are Kotlin, under `src/main/kotlin`, `src/test/kotlin`
and `src/androidTest/kotlin`. Kotlin compilation comes from AGP 9.4.1.

Lifecycle 2.9.4 and the existing AndroidX/Glide versions retain API 21 support.
Coroutines core, Android and test libraries use version 1.10.2. View Binding is
provided by the Android Gradle plugin and needs no runtime dependency.

## Building

Use Android Studio Quail 4 (2026.1.4) or newer, JDK 17 or newer, and Android SDK 36
with Build Tools 36.0.0. Set the SDK path in your untracked `local.properties`
(`sdk.dir=/path/to/Android/sdk`) or set `ANDROID_HOME`.

The project uses Android Gradle Plugin 9.4.1 and the Gradle 9.6 wrapper. It targets
Android 16 (API 36) and retains Android 5.0 (API 21) as its minimum version.
Glide 4.16.0 is used because Glide 5 requires API 23.

```sh
./gradlew build :app:assembleDebugAndroidTest
```

Local tests cover Retrofit deserialization and HTTP errors, repository mapping,
ViewModel state transitions, retries, search debouncing, cancellation and cleanup.
They use fake repositories, `runTest` and virtual coroutine test dispatchers;
no live TMDB requests are made by the tests.

Run device tests with `./gradlew :app:connectedDebugAndroidTest` when an emulator
or Android device is connected. API requests use HTTPS. The existing TMDB key is
read from the `api_key` string resource by `AppContainer`.

## Original tutorial

The original application used MVP, RxJava 2 and Retrofit:

- Part 1: https://goo.gl/oKuQuU
- Part 2: https://goo.gl/xFK8zc

![Movify screenshot](https://user-images.githubusercontent.com/7893859/34636315-a68fd92c-f2c4-11e7-82a6-719ae48fcf19.png)
