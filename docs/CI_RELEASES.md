# GitHub Actions builds and releases

The workflow at `.github/workflows/android-build-release.yml` runs on pushes to `main`/`master`, pull requests, manual dispatch, and tags beginning with `v`.

Each run:

1. Sets up Java 17 and Gradle.
2. Generates a CI-only Android debug keystore using the standard public debug credentials (`androiddebugkey` / `android`).
3. Runs unit tests and Android lint.
4. Builds the debug APK and the optimized release APK.
5. Uploads both APKs plus SHA-256 checksums as a workflow artifact.

Pushing a version tag such as `v2.2.0` additionally publishes the APKs and checksums as assets on a GitHub Release. Pull requests and branch pushes only upload workflow artifacts; they do not publish a GitHub Release.

## Important signing warning

The release APK is deliberately signed with a generated **debug key** because that was requested for this workflow. The debug key has publicly known credentials and is not a secure production signing identity. CI generates a new key for each run, so APKs from different runs will not be signature-compatible with one another. This is suitable for test builds and release-pipeline verification only, not production distribution, Play Store upload, or in-place updates from a previously signed APK.

For real releases, store a private keystore outside the repository and load it from GitHub Actions secrets. Never commit `debug.keystore`, `keystore.properties`, passwords, or a production signing key. The repository `.gitignore` excludes the local signing files.

## Downloading builds

- For branch pushes, pull requests, or manual runs, open the workflow run in GitHub and download the `comicreader-apks` artifact.
- For a version tag, open the GitHub Releases page and download the APK assets.
