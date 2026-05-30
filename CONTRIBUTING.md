# Contributing

## Adding a new Minecraft version

1. Create a version directory: `src/versioned/<version>/java/com/pikmintea/dogecoin/`
2. Copy the version-specific files (networking, client init, etc.) from an existing version
3. Update the imports and API calls for the target Minecraft version
4. Add the version to `build.gradle` and `settings.gradle`
5. Register the version in `gradle.properties` (add a `mc_<version>` property)
6. Add a `build-<version>` Gradle task
7. Test by running `./gradlew build -PmcVersion=<version>`

## Code style

- No comments in code (keep it clean)
- No emojis
- Follow existing patterns in the file you're editing
- 4-space indentation
- Use Java 21 features where appropriate

## Pull request process

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Make your changes
4. Build: `./gradlew build`
5. Submit a pull request with a clear description of the changes
