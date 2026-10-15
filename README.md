# Moix Kernel Manager

Android kernel-control manager with a Jetpack Compose / Material 3 interface.

## Build locally

Requirements: JDK 21, Android SDK platform 37, and CMake 3.22.1.

```bash
cd AppSource
chmod +x gradlew
./gradlew assembleDebug assembleRelease
```

APKs are generated under:

- `AppSource/app/build/outputs/apk/debug/`
- `AppSource/app/build/outputs/apk/release/`

## Build on GitHub Actions

Push this repository to GitHub. The workflow at `.github/workflows/build-android.yml` runs on pushes to `main` or `master`, pull requests, and manual dispatches. Open **Actions → Build Moix Kernel Manager → run → Artifacts** to download the generated APKs.

The release APK produced by this workflow is **not signed with a private release key**. It is suitable for build verification/testing; configure signing secrets before publishing a production release.

## Important native interface note

Building the APK does not guarantee kernel-control operations will work on every device. The native ioctl implementation must communicate with a real kernel driver interface supported by the target kernel. Root permission and driver support are separate requirements.
