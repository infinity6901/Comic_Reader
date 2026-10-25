# ComicReader maintenance and overhaul log

## Overhaul v10 (version 2.0, version code 11)

### Correctness fixes from the latest code review
- Fixed continuous/long-strip auto-next indexing: item count now uses the absolute strip end minus the trimmed base, and page binding/prefetch use the absolute end boundary. This prevents appended chapters from becoming an empty strip after earlier segments are dropped.
- Fixed even-page two-page spread completion checks so reaching the final spread marks the chapter read.
- Prevented incoming `open with` intents from being replayed during activity recreation, and serialized directory/favorites/history/bookmark listing jobs so stale results cannot overwrite newer tabs. Tab changes cancel deep search and clear browse-only search results.
- Made history sorting recency-first regardless of the generic library sort preference. Corrected bookmark removal wording and surfaced missing-file labels for stale favorites/bookmarks.
- Filtered macOS `__MACOSX` and `._*` resource-fork entries from comic archives, sorted folder cover images naturally, and throttled disk-thumbnail cache trimming to avoid repeated directory scans during scrolling.
- Validated backup preference values against each key's expected type, preventing malformed backups from storing a Boolean under a String preference.
- Fixed pinch pointer-up anchoring, made crop handles density-aware, protected crop saves from concurrent bitmap rotation, and recycled page-export bitmaps after use.
- Kept incoming shared comic files out of startup stale-cache cleanup so a file can survive process restoration. Added a warning when cleanup selection would remove every copy in a duplicate group.
- Updated thumbnail binding to use the adapter-provided position rather than capturing `NO_POSITION`; renamed unused catch parameters for compatibility with the project's Kotlin toolchain.
- Added regression coverage for macOS archive metadata filtering.

### v10 verification status
- Source checks, resource XML parsing, and ZIP integrity checks are run for this package.
- The standalone Kotlin utility tests are run where the local compiler and required dependencies permit.
- Full Android Gradle compilation and device testing remain unverified because Gradle 8.9 is not cached and network access is unavailable in this environment.

This update focuses on correctness and memory behavior in the image pipeline, crop workflow, and backup/export handling. The changes are intentionally narrow and preserve the existing app architecture and settings format.

## Changes

### Page rendering and memory
- When automatic border cropping creates a replacement bitmap, the original bitmap is now recycled immediately. When no crop is needed, the original is retained.
- The decoded-page LRU cache is bounded to a practical 24–128 MiB range. The upper clamp also avoids `Long` to `Int` overflow on devices reporting unusually large heaps.
- Crop-screen decoding now bounds the *longest edge* to the requested maximum. The previous two-dimension condition could leave an extremely tall or wide image full-sized when its short edge was below the limit.
- Rotating a crop image releases the replaced bitmap after the crop view is updated.
- Crop rectangles are clamped so they always have at least one pixel in each dimension.

### Error handling and resilience
- Backup export now reports a proper I/O failure if Android cannot open the chosen destination, instead of crashing on a nullable stream.
- Image export similarly handles a missing output stream as an I/O error.
- Crop loading preserves coroutine cancellation instead of swallowing it as a normal image-load failure.
- Cover thumbnail generation catches ordinary exceptions rather than swallowing fatal VM errors such as `OutOfMemoryError`.

### Thumbnail cache correctness
- Thumbnail cache keys now include requested dimensions and archive charset. A small list cover can no longer be reused as a larger grid cover, and changing archive filename encoding no longer reuses an unrelated cached result.
- Corrupt disk-cache images are deleted rather than decoded on every bind.
- Thumbnail JPEGs are written to a temporary file and renamed only after a successful encode, avoiding partially written canonical cache entries after interruption.
- Cover generation propagates coroutine cancellation and no longer swallows fatal VM errors such as `OutOfMemoryError`.
- Disk-cache trimming runs after thumbnail writes, not only at app startup.

### Decode and archive resilience
- Bitmap sampling now treats a requested dimension of `1` as unconstrained and downsamples when either constrained dimension exceeds its target, reducing avoidable oversized decodes.
- Image, PDF, ZIP, RAR, and 7z page sources reject out-of-range page requests with a clear `IOException` instead of leaking implementation-specific index exceptions.

### Regression checks
- Added JVM unit tests for natural numeric ordering of page filenames, very long digit runs, and case-insensitive comparison.
- The bounded decoded-page cache, safe thumbnail tap handling, PDF thumbnail sizing, and cancellation handling remain in place.

## Verification status

- ZIP archive integrity and source-level checks can be run locally.
- The latest source review covered thumbnail cache keying/publication, cancellation propagation, decode sampling, and page-index validation.
- A full Android/Gradle build and instrumentation run have **not** been verified in this environment because the Gradle 8.9 distribution is not cached and external network access is unavailable. Run `./gradlew testDebugUnitTest assembleDebug` on a machine with Android SDK setup and network access before installing or publishing.
- Manual device checks are still recommended for crop rotation, PDF rendering, archive navigation, backup/restore, and long-strip reading.

## Overhaul v4 — spread indexing and cancellation resilience

- Corrected the reader adapter's spread-mode bookkeeping: segments now always count physical source pages, while the RecyclerView item count is the ceiling of physical pages divided by two. Previously, spread mode could treat only the first half of the source pages as addressable, causing later pages to be blank or unavailable.
- Centralized spread row/page mapping in the pure Kotlin `SpreadLayout` helper and added JVM tests for empty, odd, and even page counts plus right-to-left visual ordering and progress mapping.
- Ensured thumbnail cover rendering rethrows coroutine cancellation instead of treating it as an ordinary unreadable-file error.
- Hardened capped stream reads against unusual `InputStream` implementations that return zero bytes without reaching EOF, avoiding a possible busy loop.

### v4 verification
- Compiled `SpreadLayout.kt` independently with the installed Kotlin compiler.
- Ran a standalone smoke test for spread counts and left-to-right/right-to-left page mapping successfully.
- ZIP integrity and source checks are performed when packaging this release.
- The complete Android Gradle build remains unverified because the required Gradle distribution cannot be downloaded in this environment.

## Overhaul v5 — stale-page invalidation when trimming chapters

- Fixed a cache correctness bug in continuous/long-strip reading: when appended chapters are removed and a different chapter is appended at the same absolute page indexes, previously decoded bitmaps could be reused for the new chapter.
- Added range invalidation for pages that become unreachable. It removes cached bitmaps, learned aspect ratios, and cancels pending decodes when trimming chapters from the end.
- Also frees cached/pending data for pages trimmed from the beginning of the strip, preventing already-read chapters from occupying the page cache unnecessarily.
- This is a targeted correctness and memory improvement; full Android build/device verification is still required.
- Added a post-decode coroutine cancellation check. Archive/PDF reads can be blocking, so cancellation may arrive while a decoder is inside a read; the returned bitmap is now recycled instead of being inserted back into the cache after its page range was invalidated.
- Bumped the app source version to 1.5 (version code 6) for this maintenance package.


## Overhaul v6 — stale decode race and resource cleanup

- Added per-page cache epochs around invalidation. A blocking archive/PDF decode that finishes after its page index is invalidated cannot repopulate the cache with an obsolete bitmap, including when a later chapter reuses the same absolute index. Cache publication and invalidation are coordinated with a shared mutation lock.
- PDF page rendering now recycles its newly allocated bitmap if native rendering fails before ownership can be returned to the caller.
- Disk thumbnail cache hits are decoded to the dimensions requested by the current view instead of a fixed 480 × 640 target, reducing memory use for small list covers.
- Temporary thumbnail files are removed on compression or publication failure.
- Bumped the app source version to 1.6 (version code 7).

### v6 verification
- Source-level checks confirm the cache-epoch validation, PDF bitmap cleanup, requested-size thumbnail decoding, and temporary-file cleanup are present.
- The pure Kotlin spread-layout and natural-order utilities are compiled and smoke-tested independently.
- Full Android/Gradle compilation and device testing remain unverified because the Gradle 8.9 distribution is unavailable offline in this environment.


## Overhaul v7 (version 1.7, version code 8)

### New reader features
- Bookmarks now support optional notes (up to 500 characters), useful for quotes, reveals, or remembering why a page matters.
- Bookmark entries display their notes and offer actions to jump to the page, edit the note, or remove the bookmark.
- Clearing a chapter's bookmarks now asks for confirmation to reduce accidental data loss.
- History and bookmark bulk-clears now use the library read-modify-write lock, preventing concurrent updates from being lost.
- Notes remain in the existing bookmark data model and therefore continue to be included in the existing backup/restore flow.

### Regression coverage
- Expanded natural-order tests for leading-zero numeric runs, mixed chapter/page numbers, and prefix ordering.

### Verification status
- ZIP integrity and resource XML well-formedness checked locally.
- Gradle test/build could not run because the wrapper distribution (Gradle 8.9) is not cached and network access is unavailable in this environment.
- Android runtime/device behavior remains unverified.


## Overhaul v8 (version 1.8, version code 9)

### New features
- Added configurable daily reading goals for pages and minutes, with progress percentages in Reading Statistics. Either target can be disabled by setting it to zero. Goal settings survive a statistics reset.
- Added a persistent **Show unread only** library filter. It hides read files while keeping folders visible so users can continue navigating into series.
- Added **Recently read** as a library sort option, based on saved reading-history timestamps.

### Daily tracking behavior
- Reading sessions increment aggregate totals and a date-scoped daily counter. The daily counter resets lazily on the first saved session of a new local day.
- Goal progress is clamped to 100% for display while retaining actual totals internally.

### Verification
- Resource XML parsing and ZIP integrity checks are run for this package.
- The Gradle test/build command was attempted, but the wrapper could not download Gradle 8.9 because `services.gradle.org` is unreachable in this environment. Full Android compilation and device testing remain unverified.

## Overhaul v9 (version 1.9, version code 10)

### Reading dashboard and sharing
- Added a rolling 365-day per-date activity log for pages and seconds read. The dashboard and Settings statistics view now show the last seven calendar days, including zero-activity days, making trends visible rather than only showing lifetime totals.
- The activity log is an additive migration. On the first new session after upgrading, existing same-day counters are used as the starting point so today's earlier progress is not lost. Historical days from before this feature cannot be reconstructed from the previous aggregate-only format and display as zero.
- Added **Share reading report**, which exports lifetime totals, recent daily activity, streaks, and configured daily goal progress through Android's share sheet.
- Added **Share bookmarks and notes** in the Bookmarks tab. It creates a plain-text list of bookmarked pages, notes, and source paths without requiring storage permissions.

### Additional maintenance
- Removed a redundant duplicate history write in deleted-path cleanup.
- Bumped the app version to 1.9 (version code 10).

### v9 verification
- Resource XML files parse successfully.
- Standalone Kotlin smoke tests for spread mapping and natural filename ordering passed.
- ZIP integrity is checked after packaging.
- Full Android Gradle compilation remains unverified because the Gradle 8.9 distribution cannot be downloaded in this network-restricted environment. Share-sheet behavior and on-device activity tracking still need device testing.


## v2.1 / Overhaul v11

- Added opt-in/out Material You dynamic colors. On Android 12+, the app can use the user's wallpaper-derived palette; older Android versions retain the bundled Material 3 fallback palette. Dynamic colors are enabled by default and can be changed under Settings → Appearance.
- Added **Pick a random unread comic** to the library menu. It chooses from the current visible listing and safely reports when no unread comic is available.
- Refined Material 3 visual rhythm with rounder cards and clearer tab indicator/ripple treatment.
- Bumped app version to 2.1 (version code 12).
- Verification performed for this pass: XML parsing, resource-reference checks, and ZIP integrity. A complete Gradle Android build and device testing remain unverified in this environment.

## v12 / CI automation additions

- Added `.github/workflows/android-build-release.yml` to run unit tests, lint, and build debug plus optimized release APKs on GitHub Actions.
- Workflow uploads named APKs and SHA-256 checksums as build artifacts; version tags (`v*`) publish those assets to GitHub Releases.
- CI generates a temporary debug keystore and signs the release variant with it, as requested. This is explicitly documented as testing-only because the debug credentials are public and the key is regenerated for each run.
- Added `docs/CI_RELEASES.md` and README instructions for artifacts, tag-based publishing, and signing limitations.
- Full build verification must happen on GitHub Actions; this local environment may not have network access to download Gradle and Android dependencies.
