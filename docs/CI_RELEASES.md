# CI builds and FOSS releases

The GitHub Actions workflow runs unit tests and lint, builds debug and release APKs, uploads them as workflow artifacts, and publishes them when a `v*` tag is pushed.

No repository signing secrets are required. The release variant uses Gradle's standard debug signing configuration to produce an installable APK for direct testing and distribution. GitHub-hosted runners are ephemeral, so the debug signing key can differ between workflow runs. As a result, an APK from a later run may not update an earlier installation; uninstalling first can erase app data.

For long-term public distribution where users should install updates over existing versions, configure a stable private release key and protect it securely.
