# FilmWatch fixed

To jest poprawiona wersja projektu.

## Co poprawiłem
- usunąłem konfigurację, która mogła powodować konflikt pluginu Kotlin,
- zamieniłem Room z KAPT na KSP,
- zostawiłem Compose + Navigation + ViewModel + Room + Retrofit.

## Jak uruchomić
1. Otwórz projekt w Android Studio.
2. Ustaw Gradle JDK = 17.
3. Skopiuj `local.properties.example` do `local.properties`.
4. Wpisz `OMDB_API_KEY`.
5. Zrób Sync.
